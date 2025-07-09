import re
from collections import defaultdict
import csv

def process_log_file(log_file_path):
    # 初始化数据结构
    document_data = defaultdict(dict)
    
    # 定义匹配模式（新增了三个模式）
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
            # 检查每种模式
            for key, pattern in patterns.items():
                match = pattern.search(line)
                if match:
                    value = int(match.group(1))
                    document = match.group(2)
                    
                    # 存储数据（累加相同键的值）
                    if key in document_data[document]:
                        document_data[document][key] += value
                    else:
                        document_data[document][key] = value
    
    # 转换为更友好的格式
    results = []
    for doc, data in document_data.items():
        result = {
            'document': doc,
            'compile_component': data.get('compile', 0),
            'locate_component': data.get('locate', 0),
            'traverse_component': data.get('traverse', 0),
            'NOD': data.get('NOD', 0),
            'DEF': data.get('DEF', 0),
            'OCC': data.get('OCC', 0),
            'LOC': data.get('LOC', 0),
            'gotoDefinition': data.get('gotoDefinition', 0),
            'rename': data.get('rename', 0),
            'completion': data.get('completion', 0)
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
        print("{:<60} {:<8} {:<8} {:<8} {:<5} {:<5} {:<5} {:<5} {:<12} {:<8} {:<10}".format(
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
    log_file_path = input("请输入日志文件路径: ")
    results = process_log_file(log_file_path)

    
    # 导出为CSV
    export_to_csv(results, "log_analysis_java-lsp.csv")
    print("\n结果已导出到 log_analysis_java-lsp.csv")