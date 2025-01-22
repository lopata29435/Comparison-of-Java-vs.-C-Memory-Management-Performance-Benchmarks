import os
import argparse
import random
import configparser


def generate_allocation_sizes(filename, sizes_length, max_power_of_two):
    sizes = [2 ** random.randint(7, max_power_of_two) for _ in range(sizes_length)]
    with open(filename, "w") as f:
        f.write("\n".join(map(str, sizes)))


def generate_access_indices(filename, indices_length, max_index):
    indices = [random.randint(0, max_index - 1) for _ in range(indices_length)]
    with open(filename, "w") as f:
        f.write("\n".join(map(str, indices)))


def generate_free_patterns(filename, length):
    patterns = [random.choice([0, 1]) for _ in range(length)]
    with open(filename, "w") as f:
        f.write("\n".join(map(str, patterns)))


def process_config(config_path, data_dir, max_power_of_two):
    config = configparser.ConfigParser()
    config.read(config_path)

    for section in config.sections():
        if "allocation_sizes_file" in config[section] or "access_indices_file" in config[section] or "free_patterns_file" in config[section] :
            section_dir = os.path.join(data_dir, section)
            os.makedirs(section_dir, exist_ok=True)
        else:
            continue

        sizes_length = int(config[section].get("sizes_length", 1000))
        indices_length = int(config[section].get("indices_length", 1000))

        if "allocation_sizes_file" in config[section]:
            sizes_file = os.path.join(section_dir, "allocation_sizes.txt")
            generate_allocation_sizes(sizes_file, sizes_length, max_power_of_two)

        if "access_indices_file" in config[section]:
            indices_file = os.path.join(section_dir, "access_indices.txt")
            generate_access_indices(indices_file, indices_length, sizes_length)

        if "free_patterns_file" in config[section]:
            patterns_file = os.path.join(section_dir, "free_patterns.txt")
            generate_free_patterns(patterns_file, sizes_length)

    print(f"Data files generated based on {config_path} in {data_dir}")


def main():
    parser = argparse.ArgumentParser(description="Generate data files for memory benchmarks based on configuration.")
    parser.add_argument("--max-power", type=int, default=12,
                        help="Maximum power of two for allocation sizes (default: 12).")

    args = parser.parse_args()

    config_path = "benchmarks_config.txt"
    data_dir = "data"

    if not os.path.exists(config_path):
        print(f"Error: Configuration file {config_path} not found.")
        return

    process_config(config_path, data_dir, args.max_power)


if __name__ == "__main__":
    main()
