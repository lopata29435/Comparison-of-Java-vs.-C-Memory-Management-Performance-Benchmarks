import re
import matplotlib.pyplot as plt
import numpy as np

def parse_benchmark(file_path, language):
    with open(file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()
    
    benchmarks = {}
    current_benchmark = None
    
    for line in lines:
        line = line.strip()
        
        if line and not any(char.isdigit() for char in line):
            current_benchmark = line

        elif line and any(char.isdigit() for char in line):
            if current_benchmark:
                if current_benchmark not in benchmarks:
                    benchmarks[current_benchmark] = {}
                benchmarks[current_benchmark][language] = float(line)
    
    return benchmarks

def merge_benchmarks(*benchmark_dicts):
    merged = {}
    for bench_dict in benchmark_dicts:
        for bench, times in bench_dict.items():
            if bench not in merged:
                merged[bench] = {}
            merged[bench].update(times)
    return merged

def format_float(val):
    return f"{val:.2f}"

def compute_percentage_faster(cpp_time, java_time):
    if cpp_time and java_time:
        try:
            return ((cpp_time - java_time) / cpp_time) * 100
        except ZeroDivisionError:
            return 0.0
    return 0.0

def generate_markdown(benchmarks, output_file):
    with open(output_file, 'w', encoding='utf-8') as file:
        file.write("| Benchmark | C++ (ms) | Java (ms) | % Faster (Java vs C++) |\n")
        file.write("|-----------|-----------|-----------|--------------------------|\n")
        
        for benchmark, times in benchmarks.items():
            cpp_time = times.get('C++')
            java_time = times.get('Java')

            cpp_str = format_float(cpp_time) if cpp_time is not None else 'N/A'
            java_str = format_float(java_time) if java_time is not None else 'N/A'

            if cpp_time is not None and java_time is not None:
                percent = compute_percentage_faster(cpp_time, java_time)
                percent_str = format_float(percent)
            else:
                percent_str = 'N/A'

            file.write(f"| {benchmark} | {cpp_str} | {java_str} | {percent_str}% |\n")

def generate_chart(benchmarks, output_file):
    languages = ['C++', 'Java']
    colors = {'C++': 'blue', 'Java': 'red'}
    
    x_labels = list(benchmarks.keys())
    x = np.arange(len(x_labels))
    width = 0.2
    
    fig, ax = plt.subplots(figsize=(12, 6))
    
    for i, lang in enumerate(languages):
        times = [benchmarks[bench].get(lang, 0) for bench in x_labels]
        ax.bar(x + i * width, times, width, label=lang, color=colors[lang])
    
    ax.set_xlabel("Benchmark")
    ax.set_ylabel("Time (ms)")
    ax.set_title("Benchmark Performance by Language")
    ax.set_xticks(x + width)
    ax.set_xticklabels(x_labels, rotation=45, ha="right")
    ax.legend()
    
    plt.tight_layout()
    plt.savefig(output_file)
    plt.show()

def main():
    cpp_file = "C++/build/CXX_Benchmark_results.txt"
    java_file = "JavaJMH/app/Java_Benchmark_results.txt"
    
    cpp_benchmarks = parse_benchmark(cpp_file, "C++")
    java_benchmarks = parse_benchmark(java_file, "Java")
    
    benchmarks = merge_benchmarks(cpp_benchmarks, java_benchmarks)
    
    output_md = "benchmark_results.md"
    output_chart = "benchmark_chart.png"
    
    generate_markdown(benchmarks, output_md)
    generate_chart(benchmarks, output_chart)
    
    print(f"Markdown таблица сохранена в {output_md}")
    print(f"График сохранен в {output_chart}")

if __name__ == "__main__":
    main()
