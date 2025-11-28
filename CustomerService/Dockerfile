# -----------------------------------------------------------------------------
# Giai đoạn 1: Builder - Biên dịch và đóng gói ứng dụng
# Sử dụng JDK (Java Development Kit) để chạy Maven
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS builder

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file POM (Project Object Model) để tải dependencies
# Bước này giúp Docker cache các dependencies nếu POM không thay đổi
COPY pom.xml .

# Tải tất cả dependencies (chỉ cần file pom.xml)
# Tùy chọn: Đặt các dependency vào một thư mục riêng biệt để tối ưu cache
RUN mvn dependency:go-offline -B

# Sao chép mã nguồn đầy đủ
COPY src ./src

# Đóng gói ứng dụng thành file JAR
RUN mvn package -DskipTests

# -----------------------------------------------------------------------------
# Giai đoạn 2: Runner - Tạo image cuối cùng
# Sử dụng JRE (Java Runtime Environment) để chạy ứng dụng (nhỏ hơn và bảo mật hơn)
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runner

# Đặt biến môi trường để tối ưu hóa hiệu suất ứng dụng trong container
ENV JAVA_TOOL_OPTIONS="-Xms512m -Xmx1024m"

# Thiết lập thư mục làm việc
WORKDIR /app

# Lấy file JAR đã được build từ giai đoạn 'builder'
# Sử dụng cách tách biệt BOOT-INF/lib (dependencies) và APP (code)
# để tối ưu hóa thời gian khởi động (layering)
COPY --from=builder /app/target/*.jar app.jar

# Spring Boot 3.x sử dụng cổng mặc định là 8080
EXPOSE 8082

# Điểm vào (Entrypoint) để chạy ứng dụng Spring Boot
# Sử dụng 'exec' form để đảm bảo tín hiệu tắt (shutdown signals) được xử lý đúng cách
ENTRYPOINT ["java", "-jar", "app.jar"]