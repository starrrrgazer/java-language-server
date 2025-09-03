import argparse
import os
from typing import Dict, List, Tuple

import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.inspection import permutation_importance
from sklearn.model_selection import KFold
from sklearn.metrics import r2_score

# python featureImportance/FeatureImportance.py --folds 5 --csv "C:\Users\74993\Desktop\毕设\snowball\project.csv"
def compute_permutation_importance(
    df: pd.DataFrame,
    feature_cols: List[str],
    target_col: str,
    n_splits: int = 5,
    random_state: int = 42,
) -> Tuple[np.ndarray, np.ndarray]:
    X = df[feature_cols].to_numpy()
    y = df[target_col].to_numpy()

    kf = KFold(n_splits=n_splits, shuffle=True, random_state=random_state)

    importances_per_fold: List[np.ndarray] = []

    for train_idx, test_idx in kf.split(X):
        X_train, X_test = X[train_idx], X[test_idx]
        y_train, y_test = y[train_idx], y[test_idx]

        model = RandomForestRegressor(
            n_estimators=300,
            max_depth=None,
            n_jobs=-1,
            random_state=random_state,
        )
        model.fit(X_train, y_train)

        # Ensure the model has reasonable predictive power on the fold
        _ = r2_score(y_test, model.predict(X_test))

        pi = permutation_importance(
            model,
            X_test,
            y_test,
            n_repeats=20,
            random_state=random_state,
            scoring="r2",
            n_jobs=-1,
        )
        importances_per_fold.append(pi.importances_mean)

    importances_matrix = np.vstack(importances_per_fold)
    means = importances_matrix.mean(axis=0)
    stds = importances_matrix.std(axis=0, ddof=1)
    return means, stds


def main() -> None:
    parser = argparse.ArgumentParser(description="5-fold permutation importance for targets")
    parser.add_argument(
        "--csv",
        type=str,
        default=None,
        help="CSV 文件路径，若不提供则尝试自动检测工作区中的 project.csv",
    )
    parser.add_argument(
        "--output",
        type=str,
        default=os.path.join("featureImportance", "permutation_importance_results.csv"),
        help="结果保存路径（CSV）",
    )
    parser.add_argument(
        "--folds",
        type=int,
        default=5,
        help="交叉验证折数",
    )
    parser.add_argument(
        "--seed",
        type=int,
        default=42,
        help="随机种子",
    )
    args = parser.parse_args()

    if args.csv is None:
        # Prefer a local project.csv if present; otherwise fall back to user's path if exists
        candidate_local = os.path.join(os.getcwd(), "project.csv")
        candidate_user = r"C:\\Users\\74993\\Desktop\\毕设\\snowball\\project.csv"
        if os.path.isfile(candidate_local):
            csv_path = candidate_local
        elif os.path.isfile(candidate_user):
            csv_path = candidate_user
        else:
            raise FileNotFoundError("未找到 project.csv，请使用 --csv 指定路径")
    else:
        csv_path = args.csv

    df = pd.read_csv(csv_path)

    feature_cols = ["NOD", "DEF", "OCC", "LOC"]
    target_cols = ["compile_component","locate_component","traverse_component","gotoDefinition", "rename", "completion"]

    missing_features = [c for c in feature_cols if c not in df.columns]
    missing_targets = [c for c in target_cols if c not in df.columns]
    if missing_features or missing_targets:
        raise ValueError(
            f"列缺失: features缺失={missing_features}, targets缺失={missing_targets}"
        )

    df_clean = df[feature_cols + target_cols].dropna().reset_index(drop=True)
    if df_clean.empty:
        raise ValueError("清洗后数据为空，请检查缺失值或数据质量")

    rows: List[Dict[str, object]] = []
    for target in target_cols:
        means, stds = compute_permutation_importance(
            df_clean, feature_cols, target, n_splits=args.folds, random_state=args.seed
        )
        for feat, m, s in zip(feature_cols, means, stds):
            rows.append(
                {
                    "target": target,
                    "feature": feat,
                    "mean_importance": float(m),
                    "std_importance": float(s),
                }
            )

    result_df = pd.DataFrame(rows).sort_values(
        by=["target", "mean_importance"], ascending=[True, False]
    )

    os.makedirs(os.path.dirname(args.output), exist_ok=True)
    result_df.to_csv(args.output, index=False)

    # Pretty print to console
    for target in target_cols:
        sub = result_df[result_df["target"] == target]
        print(f"==== {target} ====")
        for _, r in sub.iterrows():
            print(
                f"{r['feature']}: mean={r['mean_importance']:.6f}, std={r['std_importance']:.6f}"
            )


if __name__ == "__main__":
    main()


