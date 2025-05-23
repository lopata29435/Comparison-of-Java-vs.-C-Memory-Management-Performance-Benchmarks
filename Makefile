REPO_DIR := Comparison-of-Java-vs.-C-Memory-Management-Performance-Benchmarks
CPP_DIR := C++
JAVA_DIR := Java
BUILD_DIR := $(CPP_DIR)/build
BENCHMARK_LOG := benchmark_results.txt

REQUIRED_CLANG_VERSION := 18.1.3
REQUIRED_JAVA_VERSION := 21
REQUIRED_GRADLE_VERSION := 8.12
REQUIRED_CMAKE_VERSION := 3.31.5

check: check_dependencies

build: build_cpp build_javaJMH

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
		GRADLE_MAJOR=$$(echo $$GRADLE_VERSION | cut -d'.' -f1); \
		GRADLE_MINOR=$$(echo $$GRADLE_VERSION | cut -d'.' -f2); \
		GRADLE_PATCH=$$(echo $$GRADLE_VERSION | cut -d'.' -f3); \
		REQUIRED_MAJOR=8; \
		REQUIRED_MINOR=12; \
		REQUIRED_PATCH_MIN=0; \
		REQUIRED_PATCH_MAX=5; \
		if [ $$GRADLE_MAJOR -ne $$REQUIRED_MAJOR ] || { [ $$GRADLE_MINOR -lt $$REQUIRED_MINOR ] || { [ $$GRADLE_MINOR -eq $$REQUIRED_MINOR ] && [ $$GRADLE_PATCH -lt $$REQUIRED_PATCH_MIN ]; } } || { [ $$GRADLE_MINOR -eq $$REQUIRED_MINOR ] && [ $$GRADLE_PATCH -gt $$REQUIRED_PATCH_MAX ]; }; then \
			echo "Error: Gradle version $$GRADLE_VERSION is installed, but version 8.12.x is required (from 8.12 to 8.12.5)."; \
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

build_cpp:
	@echo "Building C++ part..."
	@mkdir -p $(BUILD_DIR)
	@cd $(BUILD_DIR) && cmake .. && cmake --build .

build_javaJMH:
	@echo "Building Java part..."
	@cd JavaJMH && gradle shadowJar


run:
	@echo "Running C++ benchmark..." | tee $(BENCHMARK_LOG)
	@cd $(BUILD_DIR) && ./P1 | tee -a $(BENCHMARK_LOG)

	@echo "\nRunning Java benchmark..." | tee -a $(BENCHMARK_LOG)
	@cd JavaJMH/app && java -cp build/libs/app-all.jar org.example.BenchmarkRunner | tee -a $(BENCHMARK_LOG)
	@echo "\nBenchmarks completed. Results saved in $(BENCHMARK_LOG)."

clean:
	@echo "Cleaning up..."
	@rm -rf $(BUILD_DIR)
	@cd $(JAVA_DIR) && gradle clean
	@rm -f $(BENCHMARK_LOG)


distclean: clean
	@echo "Removing repository..."
	@rm -rf $(REPO_DIR)

.PHONY: build check_dependencies build_cpp build_java run clean distclean
