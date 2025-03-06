REPO_DIR := Comparison-of-Java-vs.-C-Memory-Management-Performance-Benchmarks
CPP_DIR := C++
JAVA_DIR := Java
BUILD_DIR := $(CPP_DIR)/build
BENCHMARK_LOG := benchmark_results.txt

REQUIRED_CLANG_VERSION := 18.1.3
REQUIRED_JAVA_VERSION := 21
REQUIRED_GRADLE_VERSION := 8.12
REQUIRED_CMAKE_VERSION := 3.31.5

# Цель по умолчанию
build: check_dependencies build_cpp build_java build_javaJMH

# Проверка зависимостей
check_dependencies:
	@echo "Checking dependencies..."
	@echo "Checking C++ compiler (clang++)..."
	@if ! command -v clang++ > /dev/null; then \
		echo "Error: clang++ is not installed."; \
		exit 1; \
	else \
		CLANG_VERSION=$$(clang++ --version | head -n 1 | sed 's/.*version \([0-9]\+\.[0-9]\+\.[0-9]\+\).*/\1/'); \
		if [ "$$CLANG_VERSION" != "$(REQUIRED_CLANG_VERSION)" ]; then \
			echo "Error: clang++ version $$CLANG_VERSION is installed, but version $(REQUIRED_CLANG_VERSION) is required."; \
			exit 1; \
		else \
			echo "clang++ version $$CLANG_VERSION is OK."; \
		fi; \
	fi

	@echo "Checking Java SDK (java)..."
	@if ! command -v java > /dev/null; then \
		echo "Error: java is not installed."; \
		exit 1; \
	else \
		JAVA_VERSION=$$(java --version | head -n 1 | awk '{print $$2}' | cut -d '.' -f 1); \
		if [ "$$JAVA_VERSION" != "$(REQUIRED_JAVA_VERSION)" ]; then \
			echo "Error: Java version $$JAVA_VERSION is installed, but version $(REQUIRED_JAVA_VERSION) is required."; \
			exit 1; \
		else \
			echo "Java version $$JAVA_VERSION is OK."; \
		fi; \
	fi

	@echo "Checking Gradle..."
	@if ! command -v gradle > /dev/null; then \
		echo "Error: gradle is not installed."; \
		exit 1; \
	else \
		GRADLE_VERSION=$$(gradle --version | grep Gradle | awk '{print $$2}'); \
		if [ "$$GRADLE_VERSION" != "$(REQUIRED_GRADLE_VERSION)" ]; then \
			echo "Error: Gradle version $$GRADLE_VERSION is installed, but version $(REQUIRED_GRADLE_VERSION) is required."; \
			exit 1; \
		else \
			echo "Gradle version $$GRADLE_VERSION is OK."; \
		fi; \
	fi

	@echo "Checking CMake..."
	@if ! command -v cmake > /dev/null; then \
		echo "Error: cmake is not installed."; \
		exit 1; \
	else \
		CMAKE_VERSION=$$(cmake --version | head -n 1 | awk '{print $$3}'); \
		if [ "$$CMAKE_VERSION" != "$(REQUIRED_CMAKE_VERSION)" ]; then \
			echo "Error: CMake version $$CMAKE_VERSION is installed, but version $(REQUIRED_CMAKE_VERSION) is required."; \
			exit 1; \
		else \
			echo "CMake version $$CMAKE_VERSION is OK."; \
		fi; \
	fi

	@echo "Checking Python..."
	@if ! command -v python3 > /dev/null; then \
		echo "Error: python3 is not installed."; \
		exit 1; \
	else \
		PYTHON_VERSION=$$(python3 --version | awk '{print $$2}'); \
		PYTHON_MAJOR=$$(echo $$PYTHON_VERSION | cut -d'.' -f1); \
		PYTHON_MINOR=$$(echo $$PYTHON_VERSION | cut -d'.' -f2); \
		REQUIRED_MAJOR=3; \
		REQUIRED_MINOR=12; \
		if [ $$PYTHON_MAJOR -lt $$REQUIRED_MAJOR ] || { [ $$PYTHON_MAJOR -eq $$REQUIRED_MAJOR ] && [ $$PYTHON_MINOR -lt $$REQUIRED_MINOR ]; }; then \
			echo "Error: Python version $$PYTHON_VERSION is installed, but version $$REQUIRED_MAJOR.$$REQUIRED_MINOR or higher is required."; \
			exit 1; \
		else \
			echo "Python version $$PYTHON_VERSION is OK."; \
		fi; \
	fi

	@echo "All dependencies are OK."

# Сборка C++ части
build_cpp:
	@echo "Building C++ part..."
	@mkdir -p $(BUILD_DIR)
	@cd $(BUILD_DIR) && cmake .. && cmake --build .

# Сборка Java части
build_java:
	@echo "Building Java part..."
	@cd $(JAVA_DIR) && gradle build

# Сборка JavaJMH части
build_javaJMH:
	@echo "Building Java part..."
	@cd JavaJMH && gradle shadowJar

# Запуск бенчмарков
run:
	@echo "Running C++ benchmark..." | tee $(BENCHMARK_LOG)
	@cd $(BUILD_DIR) && ./P1 | tee -a $(BENCHMARK_LOG)

	@echo "\nRunning Java benchmark..." | tee -a $(BENCHMARK_LOG)
	@cd Java/app && java -cp build/libs/app.jar org.example.BenchmarkRunner | tee -a $(BENCHMARK_LOG)
	@echo "\nBenchmarks completed. Results saved in $(BENCHMARK_LOG)."

	@echo "\nRunning JavaJMH benchmark..." | tee -a $(BENCHMARK_LOG)
	@cd JavaJMH/app && java -cp build/libs/app-all.jar org.example.BenchmarkRunner | tee -a $(BENCHMARK_LOG)
	@echo "\nBenchmarks completed. Results saved in $(BENCHMARK_LOG)."

# Очистка
clean:
	@echo "Cleaning up..."
	@rm -rf $(BUILD_DIR)
	@cd $(JAVA_DIR) && gradle clean
	@rm -f $(BENCHMARK_LOG)

# Полная очистка (включая репозиторий)
distclean: clean
	@echo "Removing repository..."
	@rm -rf $(REPO_DIR)

.PHONY: build check_dependencies build_cpp build_java run clean distclean
