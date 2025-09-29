#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Java语言服务器性能预测脚本
使用五折交叉验证和多种机器学习算法预测gotoDefinition, rename, completion性能
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
        self.feature_columns = [
            'compile_component', 'locate_component', 'traverse_component',
            'NOD', 'DEF', 'OCC', 'LOC'
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
    
    def load_data(self):
        """加载和预处理数据"""
        print("正在加载数据...")
        self.data = pd.read_csv(self.data_file)
        
        # 提取特征和目标变量
        self.X = self.data[self.feature_columns].values
        self.y = self.data[self.target_columns].values
        
        print(f"数据加载完成: {self.X.shape[0]} 个样本, {self.X.shape[1]} 个特征")
        print(f"目标变量: {self.target_columns}")
        print(f"特征变量: {self.feature_columns}")
        
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
    
    def run_prediction(self):
        """运行完整的预测流程"""
        print("Java语言服务器性能预测")
        print("=" * 50)
        
        # 加载数据
        self.load_data()
        
        # 训练和评估模型
        self.train_and_evaluate_all_models()
        
        # 保存结果
        results_df = self.save_results_to_file()
        
        # 打印摘要
        self.print_summary()
        
        return results_df

def main():
    """主函数"""
    try:
        # 创建预测器实例
        predictor = JavaLSPPredictor('java-lsp_results.csv')
        
        # 运行预测
        results = predictor.run_prediction()
        
        print("\n预测完成！")
        
    except FileNotFoundError:
        print("错误: 找不到数据文件 'log_analysis_java-lsp.csv'")
        print("请确保数据文件存在于当前目录中")
    except Exception as e:
        print(f"发生错误: {str(e)}")

if __name__ == "__main__":
    main()
