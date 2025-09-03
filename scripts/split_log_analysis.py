#!/usr/bin/env python3
import csv
import os
from pathlib import Path


def ensure_dir(path: Path) -> None:
    path.mkdir(parents=True, exist_ok=True)


def main():
    repo_root = Path(__file__).resolve().parents[1]
    source_csv = repo_root / "log_analysis_java-lsp.csv"
    out_dir = repo_root / "zresult"
    ensure_dir(out_dir)

    targets = {
        "DEF": out_dir / "DEF.csv",
        "OCC": out_dir / "OCC.csv",
        "LOC": out_dir / "LOC.csv",
        "NOD": out_dir / "NODE.csv",  # 按用户目录命名保持一致
    }

    # 初始化写入器和打开的文件句柄
    writers = {}
    files = {}
    try:
        with source_csv.open("r", encoding="utf-8", newline="") as f:
            reader = csv.reader(f)
            header = next(reader)

            # 为每个目标文件写入表头
            for key, path in targets.items():
                files[key] = path.open("w", encoding="utf-8", newline="")
                writers[key] = csv.writer(files[key])
                writers[key].writerow(header)

            # 遍历源数据，依据第一列的路径包含的类别分发
            for row in reader:
                if not row:
                    continue
                uri = row[0]
                if "/DEF/" in uri or "\\DEF\\" in uri:
                    writers["DEF"].writerow(row)
                elif "/OCC/" in uri or "\\OCC\\" in uri:
                    writers["OCC"].writerow(row)
                elif "/LOC/" in uri or "\\LOC\\" in uri:
                    writers["LOC"].writerow(row)
                elif "/NOD/" in uri or "\\NOD\\" in uri:
                    writers["NOD"].writerow(row)
                else:
                    # 未匹配到任何类别则跳过
                    continue
    finally:
        for fh in files.values():
            try:
                fh.close()
            except Exception:
                pass


if __name__ == "__main__":
    main()


