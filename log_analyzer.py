import re
from collections import defaultdict
import csv

def process_log_file(log_file_path):
    # 初始化数据结构
    document_data = defaultdict(dict)
    
    # 定义匹配模式
    patterns = {
        'compile': re.compile(r'compile component: (\d+) document: (.+)$'),
        'locate': re.compile(r'locate component: (\d+) document: (.+)$'),
        'traverse': re.compile(r'traverse component: (\d+) document: (.+)$'),
        'NOD': re.compile(r'NOD: (\d+) document: (.+)$'),
        'DEF': re.compile(r'DEF: (\d+) document: (.+)$'),
        'OCC': re.compile(r'OCC: (\d+) document: (.+)$'),
        'LOC': re.compile(r'LOC: (\d+) document: (.+)$'),
        'gotoDefinition': re.compile(r'gotoDefinition: (\d+) document: (.+)$'),
        'rename': re.compile(r'rename: (\d+) document: (.+)$'),
        'completion': re.compile(r'completion: (\d+) document: (.+)$')
    }
    
    with open(log_file_path, 'r', encoding='utf-8') as file:
        for line in file:
            for key, pattern in patterns.items():
                match = pattern.search(line)
                if match:
                    value = int(match.group(1))
                    document = match.group(2)
                    if key in document_data[document]:
                        document_data[document][key] += value
                    else:
                        document_data[document][key] = value
    return document_data

def transpose_to_csv(document_data, output_file):
    # 获取所有文档和所有指标
    documents = list(document_data.keys())
    metrics = ['compile', 'locate', 'traverse', 'NOD', 'DEF', 'OCC', 'LOC', 
               'gotoDefinition', 'rename', 'completion']
    
    # 准备转置后的数据
    transposed_data = []
    for metric in metrics:
        row = {'Metric': metric}
        for doc in documents:
            row[doc] = document_data[doc].get(metric, 0)
        transposed_data.append(row)
    
    # 写入CSV
    with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['Metric'] + documents
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        
        writer.writeheader()
        writer.writerows(transposed_data)

if __name__ == "__main__":
    # log_file_path = input("请输入日志文件路径: ")
    log_file_path = "./ljy.log"
    document_data = process_log_file(log_file_path)
    
    # 输出转置的CSV
    transpose_to_csv(document_data, "log_analysis.csv")
    print("结果已导出到 log_analysis.csv")