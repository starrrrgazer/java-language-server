#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Java语言服务器性能预测脚本
使用五折交叉验证和多种机器学习算法预测gotoDefinition, rename, completion性能

python result_analysis.py --data results_all.csv --output ml_prediction_results_new.csv
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import KFold, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LinearRegression, Ridge, Lasso, ElasticNet
from sklearn.tree import DecisionTreeRegressor
from sklearn.svm import SVR
from sklearn.ensemble import RandomForestRegressor, GradientBoostingRegressor
from sklearn.neighbors import KNeighborsRegressor
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
import warnings
import argparse
from io import StringIO
warnings.filterwarnings('ignore')

class JavaLSPPredictor:
    """Java语言服务器性能预测器"""

    def __init__(self, data_file='log_analysis_java-lsp.csv'):
        """
        初始化预测器

        Args:
            data_file (str): 数据文件路径
        """
        self.data_file = data_file
        self.data = None
        self.X = None
        self.y = None
        self.scaler = StandardScaler()
        self.models = {}
        self.results = {}

        # 定义特征列和目标列
        # 首选特征列名；部分数据集可能使用 search_component 代替 traverse_component
        # traverse_component,traverseTimes,locate_component,search_component
        #
        self.feature_columns = [
            'traverse_component','traverseTimes', 'locate_component', 'traverse_component',
            'NOD', 'DEF', 'OCC', 'LOC', 'subcontextNum'
        ]
        self.target_columns = ['gotoDefinition', 'rename', 'completion']

        # 初始化机器学习模型
        self._initialize_models()

    def _initialize_models(self):
        """初始化机器学习模型"""
        self.models = {
            'LR': LinearRegression(),  # 线性回归
            'DT': DecisionTreeRegressor(random_state=42),  # 决策树
            'SVR': SVR(kernel='rbf', C=1.0, gamma='scale'),  # 支持向量回归
            'RR': Ridge(alpha=1.0),  # 岭回归
            'RF': RandomForestRegressor(n_estimators=100, random_state=42),  # 随机森林
            'Lasso': Lasso(alpha=1.0),  # Lasso回归
            'EN': ElasticNet(alpha=1.0, l1_ratio=0.5),  # 弹性网络
            'KNR': KNeighborsRegressor(n_neighbors=5),  # K近邻回归
            'GBR': GradientBoostingRegressor(n_estimators=100, random_state=42)  # 梯度提升回归
        }

    def _read_csv_flexibly(self, path: str) -> pd.DataFrame:
        """尝试以宽松方式读取CSV，兼容包含分段标题的results_all格式"""
        # 先尝试常规读取
        try:
            df = pd.read_csv(path)
            # 如果第一列名看起来正常就直接返回
            if 'document' in df.columns:
                return df
        except Exception:
            pass

        # 宽松解析：跳过以'='开头的分段标题，收集最后一次出现的表头以及其后的所有数据行
        with open(path, 'r', encoding='utf-8') as f:
            lines = [line.strip() for line in f if line.strip()]

        header = None
        data_lines = []
        for line in lines:
            if line.startswith('='):
                # 分段分隔符，继续
                continue
            # 识别表头
            if line.startswith('document,'):
                header = line
                continue
            # 收集数据行（包含逗号且不是分隔符）
            if ',' in line:
                data_lines.append(line)

        if not header or not data_lines:
            raise ValueError('无法从文件解析出有效的CSV表头或数据行')

        csv_text = header + "\n" + "\n".join(data_lines)
        return pd.read_csv(StringIO(csv_text))

    def load_data(self):
        """加载和预处理数据"""
        print("正在加载数据...")
        self.data = self._read_csv_flexibly(self.data_file)
        cols = list(self.data.columns)

        # 兼容没有 traverse_component 而存在 search_component 的数据
        feature_columns_resolved = self.feature_columns.copy()
        if 'traverse_component' not in cols and 'search_component' in cols:
            print("检测到缺少列 'traverse_component'，将使用 'search_component' 作为替代")
            feature_columns_resolved[2] = 'search_component'

        # 保留所有列，不删除任何列

        # 校验必需列是否存在
        missing_features = [c for c in feature_columns_resolved if c not in cols]
        missing_targets = [c for c in self.target_columns if c not in cols]
        if missing_features:
            raise ValueError(f"数据缺少必要特征列: {missing_features}")
        if missing_targets:
            raise ValueError(f"数据缺少必要目标列: {missing_targets}")

        # 提取特征和目标变量
        self.X = self.data[feature_columns_resolved].values
        self.y = self.data[self.target_columns].values

        print(f"数据加载完成: {self.X.shape[0]} 个样本, {self.X.shape[1]} 个特征")
        print(f"目标变量: {self.target_columns}")
        print(f"特征变量: {feature_columns_resolved}")

        # 数据标准化
        self.X = self.scaler.fit_transform(self.X)
        print("数据标准化完成")

    def evaluate_model(self, model, X, y, cv_folds=5):
        """
        评估单个模型

        Args:
            model: 机器学习模型
            X: 特征数据
            y: 目标数据
            cv_folds: 交叉验证折数

        Returns:
            dict: 评估结果
        """
        kfold = KFold(n_splits=cv_folds, shuffle=True, random_state=42)

        # 计算交叉验证分数
        cv_scores = cross_val_score(model, X, y, cv=kfold, scoring='neg_mean_squared_error')
        cv_mae_scores = cross_val_score(model, X, y, cv=kfold, scoring='neg_mean_absolute_error')
        cv_r2_scores = cross_val_score(model, X, y, cv=kfold, scoring='r2')

        return {
            'cv_mse_mean': -cv_scores.mean(),
            'cv_mse_std': cv_scores.std(),
            'cv_mae_mean': -cv_mae_scores.mean(),
            'cv_mae_std': cv_mae_scores.std(),
            'cv_r2_mean': cv_r2_scores.mean(),
            'cv_r2_std': cv_r2_scores.std()
        }

    def train_and_evaluate_all_models(self):
        """训练和评估所有模型"""
        print("\n开始训练和评估模型...")
        print("=" * 80)

        for target_idx, target_name in enumerate(self.target_columns):
            print(f"\n预测目标: {target_name}")
            print("-" * 50)

            y_target = self.y[:, target_idx]
            self.results[target_name] = {}

            for model_name, model in self.models.items():
                print(f"训练 {model_name} 模型...")

                # 评估模型
                eval_results = self.evaluate_model(model, self.X, y_target)

                # 存储结果
                self.results[target_name][model_name] = eval_results

                # 打印结果
                print(f"  MSE: {eval_results['cv_mse_mean']:.4f} ± {eval_results['cv_mse_std']:.4f}")
                print(f"  MAE: {eval_results['cv_mae_mean']:.4f} ± {eval_results['cv_mae_std']:.4f}")
                print(f"  R²:  {eval_results['cv_r2_mean']:.4f} ± {eval_results['cv_r2_std']:.4f}")
                print()

    def save_results_to_file(self, output_file='ml_prediction_results.csv'):
        """保存结果到CSV文件"""
        print(f"\n保存结果到文件: {output_file}")

        # 准备结果数据
        results_data = []

        for target_name in self.target_columns:
            for model_name in self.models.keys():
                result = self.results[target_name][model_name]
                results_data.append({
                    'Target': target_name,
                    'Model': model_name,
                    'CV_MSE_Mean': result['cv_mse_mean'],
                    'CV_MSE_Std': result['cv_mse_std'],
                    'CV_MAE_Mean': result['cv_mae_mean'],
                    'CV_MAE_Std': result['cv_mae_std'],
                    'CV_R2_Mean': result['cv_r2_mean'],
                    'CV_R2_Std': result['cv_r2_std']
                })

        # 创建DataFrame并保存
        results_df = pd.DataFrame(results_data)
        results_df.to_csv(output_file, index=False, encoding='utf-8-sig')

        print(f"结果已保存到 {output_file}")
        return results_df

    def print_summary(self):
        """打印结果摘要"""
        print("\n" + "=" * 80)
        print("结果摘要")
        print("=" * 80)

        for target_name in self.target_columns:
            print(f"\n{target_name} 预测结果:")
            print("-" * 40)

            # 按R²分数排序
            target_results = []
            for model_name, result in self.results[target_name].items():
                target_results.append((model_name, result['cv_r2_mean']))

            target_results.sort(key=lambda x: x[1], reverse=True)

            print("模型排名 (按R²分数):")
            for i, (model_name, r2_score) in enumerate(target_results, 1):
                mse = self.results[target_name][model_name]['cv_mse_mean']
                mae = self.results[target_name][model_name]['cv_mae_mean']
                print(f"  {i}. {model_name:6s}: R²={r2_score:.4f}, MSE={mse:.4f}, MAE={mae:.4f}")

    def run_prediction(self, output_file: str = 'ml_prediction_results.csv'):
        """运行完整的预测流程"""
        print("Java语言服务器性能预测")
        print("=" * 50)

        # 加载数据
        self.load_data()

        # 训练和评估模型
        self.train_and_evaluate_all_models()

        # 保存结果
        results_df = self.save_results_to_file(output_file)

        # 打印摘要
        self.print_summary()

        return results_df

def main():
    """主函数"""
    try:
        parser = argparse.ArgumentParser(description='Java LSP 性能预测')
        parser.add_argument('--data', '-d', default='log_analysis_java-lsp.csv', help='CSV数据文件路径')
        parser.add_argument('--output', '-o', default='ml_prediction_results.csv', help='结果输出CSV文件路径')
        args = parser.parse_args()

        # 创建预测器实例
        predictor = JavaLSPPredictor(args.data)

        # 运行预测
        results = predictor.run_prediction(output_file=args.output)

        print("\n预测完成！")
        print(f"结果文件: {args.output}")

    except FileNotFoundError:
        print("错误: 找不到数据文件")
        print("请确保数据文件存在于当前目录中")
    except Exception as e:
        print(f"发生错误: {str(e)}")

if __name__ == "__main__":
    main()
