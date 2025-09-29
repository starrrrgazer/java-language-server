import re
from collections import defaultdict
import csv
import os
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.inspection import permutation_importance
from sklearn.model_selection import KFold
import numpy as np
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
from sklearn.preprocessing import PolynomialFeatures
from sklearn.pipeline import make_pipeline

def process_log_file(log_file_path):
    from collections import defaultdict
    import os, re

    document_data = defaultdict(lambda: defaultdict(list))

    # 支持浮点数的正则 (\d+ 或 \d.xxx)
    patterns = {
        'compile': re.compile(r'compile component: (\d+(?:\.\d+)?) document: (.+)$'),
        'locate': re.compile(r'locate component: (\d+(?:\.\d+)?) document: (.+)$'),
        'traverse': re.compile(r'traverse component: (\d+(?:\.\d+)?) document: (.+)$'),
        'NOD': re.compile(r'NOD: (\d+(?:\.\d+)?) document: (.+)$'),
        'DEF': re.compile(r'DEF: (\d+(?:\.\d+)?) document: (.+)$'),
        'OCC': re.compile(r'OCC: (\d+(?:\.\d+)?) document: (.+)$'),
        'LOC': re.compile(r'LOC: (\d+(?:\.\d+)?) document: (.+)$'),
        'gotoDefinition': re.compile(r'gotoDefinition: (\d+(?:\.\d+)?) document: (.+)$'),
        'rename': re.compile(r'rename: (\d+(?:\.\d+)?) document: (.+)$'),
        'completion': re.compile(r'completion: (\d+(?:\.\d+)?) document: (.+)$'),
        'subcontextNum': re.compile(r'subcontextNum: (\d+(?:\.\d+)?) document: (.+)$')
    }

    # 逐行读取 log 文件
    with open(log_file_path, 'r', encoding='utf-8') as file:
        for line in file:
            for key, pattern in patterns.items():
                match = pattern.search(line)
                if match:
                    value = float(match.group(1))   # ✅ 改成 float
                    document = match.group(2)
                    document_data[document][key].append(value)

    # 计算去掉最大最小的平均值
    results = []
    for doc, data in document_data.items():
        def avg_drop_min_max(vals):
            if len(vals) > 2:
                vals_sorted = sorted(vals)
                return sum(vals_sorted[1:-1]) / (len(vals) - 2)
            elif vals:
                return sum(vals) / len(vals)
            else:
                return 0.0

        result = {'document': doc}

        # 提取 dataset 名
        base = os.path.basename(doc)  # e.g. DEF_80.java
        dataset = base.split("_")[0] if "_" in base else "UNKNOWN"
        result['dataset'] = dataset

        # 每个指标
        result['compile_component'] = avg_drop_min_max(data.get('compile', []))
        result['locate_component'] = avg_drop_min_max(data.get('locate', []))
        result['traverse_component'] = avg_drop_min_max(data.get('traverse', []))
        result['NOD'] = avg_drop_min_max(data.get('NOD', []))
        result['DEF'] = avg_drop_min_max(data.get('DEF', []))
        result['OCC'] = avg_drop_min_max(data.get('OCC', []))
        result['LOC'] = avg_drop_min_max(data.get('LOC', []))
        result['gotoDefinition'] = avg_drop_min_max(data.get('gotoDefinition', []))
        result['rename'] = avg_drop_min_max(data.get('rename', []))
        result['completion'] = avg_drop_min_max(data.get('completion', []))
        result['subcontextNum'] = avg_drop_min_max(data.get('subcontextNum', []))

        results.append(result)

    return results




def merge_logs(metric_folder, output_log):
    """合并某个指标文件夹下所有 souffle.log"""
    with open(output_log, "w", encoding="utf-8") as out:
        for subdir, _, files in os.walk(metric_folder):
            if "souffle.log" in files:
                log_path = os.path.join(subdir, "souffle.log")
                with open(log_path, "r", encoding="utf-8") as f:
                    out.write(f.read())
                    out.write("\n")


def export_multi_to_csv(results, output_file="parsed_results.csv"):
    """把结果导出到一个 CSV 文件"""
    fieldnames = [
        'document', 'dataset',
        'compile_component', 'locate_component',
        'traverse_component', 'NOD', 'DEF', 'OCC', 'LOC',
        'gotoDefinition', 'rename', 'completion', 'subcontextNum'
    ]
    with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        for r in results:
            writer.writerow({k: r.get(k, "") for k in fieldnames})

    print(f"[完成] 已导出结果到 {output_file}")

def analyze_feature_importance(results, output_file="feature_importance.csv"):
    """基于 permutation importance + KFold 计算特征重要性"""
    features = ['LOC', 'DEF', 'OCC', 'NOD']
    targets = [
        'compile_component', 'locate_component', 'traverse_component',
        'gotoDefinition', 'rename', 'completion'
    ]

    # 转换为 DataFrame
    df = pd.DataFrame(results).drop_duplicates(subset=["document"])
    df = df.fillna(0)  # 避免 NaN

    if df.empty:
        print("没有可用的数据来计算特征重要性！")
        return

    X = df[features].to_numpy()
    importance_rows = []

    for target in targets:
        if target not in df.columns:
            print(f"[跳过] {target} 不在数据中")
            continue

        y = df[target].to_numpy()

        if len(np.unique(y)) <= 1:
            print(f"[跳过] {target} 没有有效数据")
            continue

        # KFold
        KF = KFold(n_splits=5, shuffle=True, random_state=42)
        avg_weights = np.zeros(len(features))

        for train_idx, test_idx in KF.split(X):
            xtrain, xtest = X[train_idx], X[test_idx]
            ytrain, ytest = y[train_idx], y[test_idx]

            model = RandomForestRegressor(n_estimators=200, random_state=42)
            model.fit(xtrain, ytrain)

            result = permutation_importance(
                model, xtest, ytest, n_repeats=10, random_state=42
            )

            avg_weights += result['importances_mean']

        avg_weights /= KF.get_n_splits()

        # 保存一行：target + 四个特征的重要性
        row = {"target": target}
        for f, w in zip(features, avg_weights):
            row[f] = w
        importance_rows.append(row)

    # 写 CSV
    fieldnames = ["target"] + features
    with open(output_file, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(importance_rows)

    print(f"[完成] 基于 KFold 的特征重要性已保存到 {output_file}")



def linear_regression_fit(results, target, train_dataset, x_param,
                          summary_file="linear_regression_results.csv",
                          detail_file="detailed_predictions.csv"):
    """
    用指定数据集 (train_dataset) 训练一元线性回归 (y = kx + b)，
    在其余数据集以及 ALL(全部数据) 上测试预测效果。

    参数:
        results: list[dict]，process_log_file 解析得到的结果
        target: str，要预测的目标列
        train_dataset: str，用作训练集的数据集名称（例如 'LOC'）
        x_param: str，自变量
    """

    df = pd.DataFrame(results).drop_duplicates(subset=["document"])

    # 按 dataset 分组
    datasets = {d: g for d, g in df.groupby("dataset")}

    if train_dataset not in datasets:
        raise ValueError(f"训练集 {train_dataset} 不存在！可选: {list(datasets.keys())}")

    train_df = datasets[train_dataset]
    if target not in train_df.columns or x_param not in train_df.columns:
        raise ValueError(f"训练数据缺少列: {target} 或 {x_param}")

    # 训练回归模型
    x_train = train_df[[x_param]].values
    y_train = train_df[target].values
    model = LinearRegression()
    model.fit(x_train, y_train)
    k = model.coef_[0]
    b = model.intercept_

    # summary/detail 文件表头
    summary_fields = [
        "train_dataset", "x_param", "target", "test_dataset",
        "k", "b", "MSE", "RMAE", "R2", "mean_real", "mean_pred"
    ]
    detail_fields = [
        "train_dataset", "x_param", "target", "test_dataset",
        "document", "real_value", "pred_value"
    ]

    with open(summary_file, "w", newline="", encoding="utf-8") as sf, \
         open(detail_file, "w", newline="", encoding="utf-8") as df_out:
        summary_writer = csv.DictWriter(sf, fieldnames=summary_fields)
        detail_writer = csv.DictWriter(df_out, fieldnames=detail_fields)
        summary_writer.writeheader()
        detail_writer.writeheader()

        # 其余 dataset 测试
        for test_dataset, test_df in datasets.items():
            if test_dataset == train_dataset:
                continue
            if target not in test_df.columns or x_param not in test_df.columns:
                continue

            real_y = test_df[target].values
            x_test = test_df[[x_param]].values
            pred_y = model.predict(x_test)

            mse = mean_squared_error(real_y, pred_y)
            mae = mean_absolute_error(real_y, pred_y)
            rmae = mae / np.mean(real_y) if np.mean(real_y) != 0 else np.nan
            r2 = r2_score(real_y, pred_y)
            mean_real = np.mean(real_y)
            mean_pred = np.mean(pred_y)

            summary_writer.writerow({
                "train_dataset": train_dataset,
                "x_param": x_param,
                "target": target,
                "test_dataset": test_dataset,
                "k": k,
                "b": b,
                "MSE": mse,
                "RMAE": rmae,
                "R2": r2,
                "mean_real": mean_real,
                "mean_pred": mean_pred
            })

            for doc, real, pred in zip(test_df["document"], real_y, pred_y):
                detail_writer.writerow({
                    "train_dataset": train_dataset,
                    "x_param": x_param,
                    "target": target,
                    "test_dataset": test_dataset,
                    "document": doc,
                    "real_value": real,
                    "pred_value": pred
                })

        # ========= ALL: 全部数据一起作为测试集 =========
        real_y = df[target].values
        x_test = df[[x_param]].values
        pred_y = model.predict(x_test)

        mse = mean_squared_error(real_y, pred_y)
        mae = mean_absolute_error(real_y, pred_y)
        rmae = mae / np.mean(real_y) if np.mean(real_y) != 0 else np.nan
        r2 = r2_score(real_y, pred_y)
        mean_real = np.mean(real_y)
        mean_pred = np.mean(pred_y)

        summary_writer.writerow({
            "train_dataset": train_dataset,
            "x_param": x_param,
            "target": target,
            "test_dataset": "ALL",  # 注意: ALL 是整体数据，不是某个 dataset
            "k": k,
            "b": b,
            "MSE": mse,
            "RMAE": rmae,
            "R2": r2,
            "mean_real": mean_real,
            "mean_pred": mean_pred
        })

        for doc, real, pred in zip(df["document"], real_y, pred_y):
            detail_writer.writerow({
                "train_dataset": train_dataset,
                "x_param": x_param,
                "target": target,
                "test_dataset": "ALL",
                "document": doc,
                "real_value": real,
                "pred_value": pred
            })

    print(f"[完成] {target} ~ {x_param} (训练集: {train_dataset}) 的结果已保存到 {summary_file} 和 {detail_file}")
    return k, b, df






def derived_prediction_eval(results, k_compile, b_compile, k_locate, b_locate,
                            summary_file="derived_results_summary.csv",
                            detail_file="derived_results_detail.csv"):
    """
    基于已有的线性回归结果，计算 rename_pred 和 completion_pred，
    并输出 summary 和 detail。
    - rename_pred = locate_component_pred
    - completion_pred = compile_component_pred
    """

    df = pd.DataFrame(results).drop_duplicates(subset=["document"])
    datasets = {d: g for d, g in df.groupby("dataset")}
    datasets["ALL"] = df  # 加入整体测试集

    summary_fields = [
        "target", "test_dataset", "MSE", "MAE", "RMAE", "R2", "mean_real", "mean_pred"
    ]
    detail_fields = [
        "target", "test_dataset", "document", "real_value", "pred_value"
    ]

    with open(summary_file, "w", newline="", encoding="utf-8") as sf, \
         open(detail_file, "w", newline="", encoding="utf-8") as df_out:
        summary_writer = csv.DictWriter(sf, fieldnames=summary_fields)
        detail_writer = csv.DictWriter(df_out, fieldnames=detail_fields)
        summary_writer.writeheader()
        detail_writer.writeheader()

        # ========== rename ==========
        for test_dataset, test_df in datasets.items():
            if "rename" not in test_df.columns or "OCC" not in test_df.columns:
                continue
            real_y = test_df["rename"].values
            x = test_df[["OCC"]].values
            pred_y = k_locate * x.flatten() + b_locate  # rename_pred = locate_pred

            mse = mean_squared_error(real_y, pred_y)
            mae = mean_absolute_error(real_y, pred_y)
            rmae = mae / np.mean(real_y) if np.mean(real_y) != 0 else np.nan
            r2 = r2_score(real_y, pred_y)
            mean_real = np.mean(real_y)
            mean_pred = np.mean(pred_y)

            summary_writer.writerow({
                "target": "rename",
                "test_dataset": test_dataset,
                "MSE": mse, "MAE": mae, "RMAE": rmae,
                "R2": r2, "mean_real": mean_real, "mean_pred": mean_pred
            })

            for doc, real, pred in zip(test_df["document"], real_y, pred_y):
                detail_writer.writerow({
                    "target": "rename",
                    "test_dataset": test_dataset,
                    "document": doc,
                    "real_value": real,
                    "pred_value": pred
                })

        # ========== gotoDefinition ==========
        for test_dataset, test_df in datasets.items():
            if "gotoDefinition" not in test_df.columns or "OCC" not in test_df.columns:
                continue
            real_y = test_df["gotoDefinition"].values
            x = test_df[["OCC"]].values
            pred_y = k_locate * x.flatten() + b_locate  # gotoDefinition_pred = locate_pred

            mse = mean_squared_error(real_y, pred_y)
            mae = mean_absolute_error(real_y, pred_y)
            rmae = mae / np.mean(real_y) if np.mean(real_y) != 0 else np.nan
            r2 = r2_score(real_y, pred_y)
            mean_real = np.mean(real_y)
            mean_pred = np.mean(pred_y)

            summary_writer.writerow({
                "target": "gotoDefinition",
                "test_dataset": test_dataset,
                "MSE": mse, "MAE": mae, "RMAE": rmae,
                "R2": r2, "mean_real": mean_real, "mean_pred": mean_pred
            })

            for doc, real, pred in zip(test_df["document"], real_y, pred_y):
                detail_writer.writerow({
                    "target": "gotoDefinition",
                    "test_dataset": test_dataset,
                    "document": doc,
                    "real_value": real,
                    "pred_value": pred
                })

        # ========== completion ==========
        for test_dataset, test_df in datasets.items():
            if "completion" not in test_df.columns or "LOC" not in test_df.columns:
                continue
            real_y = test_df["completion"].values
            x = test_df[["LOC"]].values
            pred_y = k_compile * x.flatten() + b_compile  # completion_pred = compile_pred

            mse = mean_squared_error(real_y, pred_y)
            mae = mean_absolute_error(real_y, pred_y)
            rmae = mae / np.mean(real_y) if np.mean(real_y) != 0 else np.nan
            r2 = r2_score(real_y, pred_y)
            mean_real = np.mean(real_y)
            mean_pred = np.mean(pred_y)

            summary_writer.writerow({
                "target": "completion",
                "test_dataset": test_dataset,
                "MSE": mse, "MAE": mae, "RMAE": rmae,
                "R2": r2, "mean_real": mean_real, "mean_pred": mean_pred
            })

            for doc, real, pred in zip(test_df["document"], real_y, pred_y):
                detail_writer.writerow({
                    "target": "completion",
                    "test_dataset": test_dataset,
                    "document": doc,
                    "real_value": real,
                    "pred_value": pred
                })

    print(f"[完成] 派生预测 (rename, completion) 的结果已保存到 {summary_file} 和 {detail_file}")




if __name__ == "__main__":
    """主函数：默认读取当前目录下的 opt.log 文件"""
    log_file = "1759052970613.log"
    if not os.path.exists(log_file):
        print(f"未找到 {log_file} 文件！")

    print(f"[读取] {log_file}")
    results = process_log_file(log_file)

    # 导出 CSV
    csv_name = os.path.splitext(log_file)[0] + "_results.csv"
    export_multi_to_csv(results, csv_name)
    
    # 计算特征重要性
    analyze_feature_importance(results, "feature_importance.csv")
    
    k_c, b_c, _ = linear_regression_fit(results, target="compile_component", train_dataset="LOC", x_param="LOC",summary_file="compile_predict_summary.csv",detail_file="compile_predict_summary.csv")
    # linear_regression_fit(results, target="traverse_component", train_dataset="LOC", x_param="LOC",summary_file="traverse_predict_summary.csv",detail_file="traverse_predict_summary.csv")
    k_l, b_l, _ = linear_regression_fit(results, target="locate_component", train_dataset="OCC", x_param="OCC",summary_file="locate_predict_summary.csv",detail_file="locate_predict_summary.csv")
    derived_prediction_eval(results, k_c, b_c, k_l, b_l)
    # linear_regression_fit(results, target="rename", train_dataset="LOC", x_param="LOC")
    # k,b,all_df = linear_regression_fit(all_results, target="search_component", feature="OCC",x_param="subcontextNum")
    # model = train_and_validate_cnum(all_results, k, b)
    # model2 = train_and_validate_cnum_completion(all_results, k, b)
    
    
    # linear_regression_fit(df, target="rename", feature="DEF")
    # linear_regression_fit(df, target="completion", feature="LOC")
    
    
    
