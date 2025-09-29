import re
from collections import defaultdict
import csv

def _trimmed_avg(stats):
    # stats: (sum, count, min_val, max_val)
    if not stats:
        return 0.0
    s, c, mn, mx = stats
    if c <= 0:
        return 0.0
    if c == 1:
        return s
    if c == 2:
        return s / 2
    return (s - mn - mx) / (c - 2)

def process_log_file(log_file_path):
    # 初始化数据结构
    document_data = defaultdict(dict)

    # 定义匹配模式（支持整数和小数，含可选正负号）
    patterns = {
        'compile': re.compile(r'compile component: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'locate': re.compile(r'locate component: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'traverse': re.compile(r'traverse component: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'NOD': re.compile(r'NOD: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'DEF': re.compile(r'DEF: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'OCC': re.compile(r'OCC: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'LOC': re.compile(r'LOC: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'gotoDefinition': re.compile(r'gotoDefinition: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'rename': re.compile(r'rename: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$'),
        'completion': re.compile(r'completion: ([+-]?(?:\d+(?:\.\d+)?|\.\d+)) document: (.+)$')
    }
    
    with open(log_file_path, 'r', encoding='utf-8') as file:
        for line in file:
            # 检查每种模式
            for key, pattern in patterns.items():
                match = pattern.search(line)
                if match:
                    value = float(match.group(1))
                    document = match.group(2)
                    
                    # 存储数据（累加相同键的值，并记录累加次数，同时维护最小和最大）
                    if key in document_data[document]:
                        s, c, mn, mx = document_data[document][key]
                        s += value
                        c += 1
                        mn = value if value < mn else mn
                        mx = value if value > mx else mx
                        document_data[document][key] = (s, c, mn, mx)
                    else:
                        document_data[document][key] = (value, 1, value, value)
    
    # 转换为更友好的格式
    results = []
    for doc, data in document_data.items():
        result = {
            'document': doc,
            'compile_component': _trimmed_avg(data.get('compile')),
            'locate_component': _trimmed_avg(data.get('locate')),
            'traverse_component': _trimmed_avg(data.get('traverse')),
            'NOD': _trimmed_avg(data.get('NOD')),
            'DEF': _trimmed_avg(data.get('DEF')),
            'OCC': _trimmed_avg(data.get('OCC')),
            'LOC': _trimmed_avg(data.get('LOC')),
            'gotoDefinition': _trimmed_avg(data.get('gotoDefinition')),
            'rename': _trimmed_avg(data.get('rename')),
            'completion': _trimmed_avg(data.get('completion'))
        }
        results.append(result)
    
    return results

def print_results(results):
    # 调整列宽以适应新增的列
    print("文档分析结果:")
    print("{:<60} {:<8} {:<8} {:<8} {:<5} {:<5} {:<5} {:<5} {:<12} {:<8} {:<10}".format(
        "Document", "Compile", "Locate", "Traverse", "NOD", "DEF", "OCC", "LOC", 
        "GoToDef", "Rename", "Completion"))
    print("-" * 130)
    
    for result in results:
        print("{:<60} {:<10.2f} {:<10.2f} {:<10.2f} {:<8.2f} {:<8.2f} {:<8.2f} {:<8.2f} {:<12.2f} {:<10.2f} {:<12.2f}".format(
            result['document'],
            result['compile_component'],
            result['locate_component'],
            result['traverse_component'],
            result['NOD'],
            result['DEF'],
            result['OCC'],
            result['LOC'],
            result['gotoDefinition'],
            result['rename'],
            result['completion']))

def export_to_csv(results, output_file):
    with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['document', 'compile_component', 'locate_component', 
                     'traverse_component', 'NOD', 'DEF', 'OCC', 'LOC',
                     'gotoDefinition', 'rename', 'completion']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        
        writer.writeheader()
        for result in results:
            writer.writerow(result)

if __name__ == "__main__":
    log_file_path = "ljy.log"
    results = process_log_file(log_file_path)

    csv_name = "new_log.csv"
    # 导出为CSV
    export_to_csv(results, "new_log.csv")
    print("\n结果已导出到 " + csv_name)