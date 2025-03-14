import re
import matplotlib.pyplot as plt
import numpy as np

def parse_benchmark(file_path, language):
    with open(file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()
    
    benchmarks = {}
    current_benchmark = None
    
    for line in lines:
        # Убираем лишние пробелы и символы новой строки
        line = line.strip()
        
        # Если строка не пустая и не содержит цифр, это название бенчмарка
        if line and not any(char.isdigit() for char in line):
            current_benchmark = line
        # Если строка содержит цифры, это время выполнения
        elif line and any(char.isdigit() for char in line):
            if current_benchmark:
                if current_benchmark not in benchmarks:
                    benchmarks[current_benchmark] = {}
                benchmarks[current_benchmark][language] = float(line)
    
    return benchmarks

def parse_jmh_benchmark(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()
    
    benchmarks = {}
    current_benchmark = None
    
    # Сопоставление названий бенчмарков из файла с заданным списком
    benchmark_mapping = {
        "ByteBench.measureAllocation": "Byte bench allocation",
        "ByteBench.measureDeallocation": "Byte bench deallocation",
        "ComplexObjectBench.measureAllocateComplex": "Complex object bench allocation and deallocation",
        "ComplexObjectBench.measureAllocatePrimitive": "Primitive object bench allocation and deallocation",
        "AllocatorBench.runBenchmark": "Allocator bench",
        "AllocatorThreadBench.runBenchmark": "Allocator thread bench",
        "MemoryFragmentationBench.measureFragmentation": "Memory fragmentation bench",
        "RecursiveAllocationBench.measureRecursiveAllocation": "Recursive allocation bench",
        "MemoryAccessBench.measureMemoryAccess": "Memory access bench"
    }
    
    for line in lines:
        # Поиск заголовка бенчмарка
        header_match = re.match(r'-+(.+)-+', line)
        if header_match:
            current_benchmark = header_match.group(1).strip()
        
        # Поиск строки с результатами бенчмарка
        jmh_match = re.search(r'(\S+\.\S+)\s+.*?\s+([\d\.e\-]+)(?:\s±\s[\d\.e\-]+)?\s+ms/op', line)
        if jmh_match:
            benchmark_name = jmh_match.group(1)
            score = jmh_match.group(2)
            
            # Сопоставляем с заданным списком бенчмарков
            if benchmark_name in benchmark_mapping:
                mapped_name = benchmark_mapping[benchmark_name]
                benchmarks[mapped_name] = {'JavaJMH': float(score)}
    
    return benchmarks

def merge_benchmarks(*benchmark_dicts):
    merged = {}
    for bench_dict in benchmark_dicts:
        for bench, times in bench_dict.items():
            if bench not in merged:
                merged[bench] = {}
            merged[bench].update(times)
    return merged

def generate_markdown(benchmarks, output_file):
    languages = ['C++', 'Java', 'JavaJMH']
    
    with open(output_file, 'w', encoding='utf-8') as file:
        file.write("| Benchmark | C++ | Java | JavaJMH |\n")
        file.write("|-----------|------|------|--------|\n")
        for benchmark, times in benchmarks.items():
            file.write(f"| {benchmark} | {times.get('C++', 'N/A')} | {times.get('Java', 'N/A')} | {times.get('JavaJMH', 'N/A')} |\n")

def generate_chart(benchmarks, output_file):
    languages = ['C++', 'Java', 'JavaJMH']
    colors = {'C++': 'blue', 'Java': 'red', 'JavaJMH': 'green'}
    
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
    cpp_file = "C++/build/CXX_Benchmark_resultssd.txt"
    java_file = "Java/app/Java_Benchmark_results.txt"
    jmh_file = "benchmark_results_javajmh.txt"
    
    cpp_benchmarks = parse_benchmark(cpp_file, "C++")
    java_benchmarks = parse_benchmark(java_file, "Java")
    jmh_benchmarks = parse_jmh_benchmark(jmh_file)
    
    benchmarks = merge_benchmarks(cpp_benchmarks, java_benchmarks, jmh_benchmarks)
    
    output_md = "benchmark_results.md"
    output_chart = "benchmark_chart.png"
    
    generate_markdown(benchmarks, output_md)
    generate_chart(benchmarks, output_chart)
    
    print(f"Markdown таблица сохранена в {output_md}")
    print(f"График сохранен в {output_chart}")

if __name__ == "__main__":
    main()
