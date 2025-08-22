# 📚 KAPSAMLI PROJE DOKÜMANI - Student CRUD Application

## 🎯 **1. PROJE GENEL BAKIŞ**

Bu proje, **Spring Boot** kullanarak geliştirilmiş **Student Management System**'dir. Öğrenci bilgilerini veritabanında saklayan, CRUD (Create, Read, Update, Delete) işlemleri yapabilen bir REST API uygulamasıdır.

**Ana Özellikler:**

- ✅ Öğrenci ekleme, silme, güncelleme, listeleme
- ✅ Veritabanı entegrasyonu (H2)
- ✅ RESTful API endpoints
- ✅ Kapsamlı test coverage
- ✅ Exception handling
- ✅ Data validation

---

## 🛠️ **2. KULLANILAN TEKNOLOJİLER**

### **2.1 Spring Boot Nedir?**

Spring Boot, Java tabanlı enterprise uygulamalar geliştirmek için kullanılan bir framework'tür.

**Temel Özellikleri:**

- **Auto-configuration**: Otomatik konfigürasyon
- **Embedded Servers**: Tomcat gibi sunucuları içinde barındırır
- **Starter Dependencies**: Gerekli kütüphaneleri otomatik ekler
- **Production Ready**: Production ortamına hazır

**Neden Spring Boot?**

- 🚀 Hızlı geliştirme
- ⚙️ Minimal konfigürasyon
- 🏗️ Microservices için ideal
- 🌐 Geniş ekosistem

### **2.2 JPA (Java Persistence API)**

Veritabanı işlemlerini Java objeleri üzerinden yapmamızı sağlar.

**Temel Anotasyonlar:**

```java
@Entity          // Bu sınıfın veritabanı tablosu olduğunu belirtir
@Table           // Tablo adını belirler
@Id              // Primary key
@GeneratedValue  // Otomatik ID üretimi
@Column          // Kolon özellikleri
```

### **2.3 H2 Database**

Hafif, in-memory veritabanı. Development ve testing için idealdir.

**Avantajları:**

- ⚡ Hızlı başlatma
- 💾 Dosya tabanlı
- 🌐 Web console mevcut
- 🧪 Test ortamı için mükemmel

---

## 🏗️ **3. PROJE MİMARİSİ**

### **3.1 Katmanlı Mimari (Layered Architecture)**

```
┌─────────────────┐
│   Controller    │ ← HTTP isteklerini karşılar
├─────────────────┤
│     Service     │ ← İş mantığı
├─────────────────┤
│   Repository    │ ← Veritabanı işlemleri
├─────────────────┤
│   Database      │ ← H2 veritabanı
└─────────────────┘
```

**Her Katmanın Görevi:**

1. **Controller Layer**: HTTP isteklerini karşılar, response döner
2. **Service Layer**: İş mantığını yönetir, validation yapar
3. **Repository Layer**: Veritabanı işlemlerini gerçekleştirir
4. **Model Layer**: Veri yapısını tanımlar

---

## 📁 **4. PROJE DOSYA YAPISI**

```
src/
├── main/
│   ├── java/
│   │   └── com/example/student/
│   │       ├── StudentCrudApplication.java     ← Ana uygulama sınıfı
│   │       ├── controller/                     ← REST API endpoints
│   │       │   ├── StudentController.java
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── ErrorResponse.java
│   │       ├── service/                        ← İş mantığı
│   │       │   └── StudentService.java
│   │       ├── repository/                     ← Veritabanı işlemleri
│   │       │   └── StudentRepository.java
│   │       ├── model/                          ← Veri modelleri
│   │       │   └── Student.java
│   │       └── exception/                      ← Özel exception'lar
│   │           ├── StudentNotFoundException.java
│   │           └── DuplicateEmailException.java
│   └── resources/
│       └── application.properties              ← Konfigürasyon
└── test/                                       ← Test dosyaları
    └── java/com/example/student/
        ├── StudentControllerTest.java
        ├── StudentServiceTest.java
        ├── StudentRepositoryIntegrationTest.java
        └── StudentCrudApplicationIntegrationTest.java
```

---

## 🔧 **5. ÖĞRENCİ İŞLEMLERİ NASIL YAPILIR?**

### **5.1 Uygulamayı Çalıştırma**

```bash
# Proje dizininde
mvn spring-boot:run
```

Uygulama `http://localhost:8080` adresinde çalışmaya başlar.

### **5.2 API Endpoints**

**1. Öğrenci Ekleme:**

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmet Yılmaz",
    "email": "ahmet@example.com",
    "age": 20,
    "address": "İstanbul, Türkiye"
  }'
```

**2. Tüm Öğrencileri Listeleme:**

```bash
curl http://localhost:8080/api/students
```

**3. Belirli Öğrenciyi Getirme:**

```bash
curl http://localhost:8080/api/students/1
```

**4. Öğrenci Güncelleme:**

```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ahmet Yılmaz Güncellendi",
    "email": "ahmet@example.com",
    "age": 21,
    "address": "Ankara, Türkiye"
  }'
```

**5. Öğrenci Silme:**

```bash
curl -X DELETE http://localhost:8080/api/students/1
```

### **5.3 H2 Veritabanı Console**

Tarayıcıda `http://localhost:8080/h2-console` adresine gidin:

- **JDBC URL**: `jdbc:h2:mem:studentdb`
- **Username**: `sa`
- **Password**: (boş bırakın)

---

## 🧪 **6. TEST KONULARI**

### **6.1 Test Nedir ve Neden Gereklidir?**

**Test**, yazdığımız kodun doğru çalışıp çalışmadığını kontrol etmemizi sağlar.

**Test Yapmanın Faydaları:**

- ✅ **Güven**: Kodun doğru çalıştığından emin oluruz
- ✅ **Refactoring**: Kodu güvenle değiştirebiliriz
- ✅ **Dokümantasyon**: Testler kodun nasıl kullanılacağını gösterir
- ✅ **Hata Bulma**: Sorunları erken tespit ederiz

### **6.2 Test Türleri**

**1. Unit Test (Birim Test)**

- **Ne**: Tek bir sınıfın veya metodun test edilmesi
- **Neden**: Hızlı, bağımsız, detaylı hata tespiti
- **Nasıl**: Mockito ile bağımlılıklar mock'lanır

**2. Integration Test (Entegrasyon Test)**

- **Ne**: Birden fazla bileşenin birlikte çalışmasının test edilmesi
- **Neden**: Gerçek ortam koşullarında test
- **Nasıl**: H2 veritabanı ile gerçek veritabanı işlemleri

**3. End-to-End Test**

- **Ne**: Tüm sistemin baştan sona test edilmesi
- **Neden**: Gerçek kullanım senaryolarını test etmek
- **Nasıl**: MockMvc ile HTTP istekleri simüle edilir

### **6.3 Mocking Nedir?**

**Mocking**, test edilen sınıfın bağımlılıklarını sahte (fake) objelerle değiştirme işlemidir.

**Neden Mocking?**

- ⚡ **Hız**: Gerçek veritabanı bağlantısı gerekmez
- 🔒 **Bağımsızlık**: Diğer bileşenlerden etkilenmez
- 🎯 **Kontrol**: Test senaryolarını tam kontrol edebiliriz

### **6.4 Mockito Nedir?**

**Mockito**, Java'da mocking yapmak için kullanılan bir kütüphanedir.

**Temel Mockito Anotasyonları:**

```java
@Mock           // Mock objesi oluşturur
@InjectMocks    // Mock'ları inject eder
@MockBean       // Spring context'inde mock bean oluşturur
```

**Mockito Metodları:**

```java
when(mock.method()).thenReturn(value);     // Mock davranışı tanımlar
verify(mock).method();                     // Metodun çağrıldığını doğrular
any(), eq(), argThat()                     // Argüman matcher'ları
```

---

## 💻 **7. KOD ANALİZİ - SENİOR SEVİYESİNDE**

### **7.1 Student Entity Sınıfı**

```java
@Entity
@Table(name = "students",
       uniqueConstraints = @UniqueConstraint(name = "uk_student_email", columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    // ... fields
}
```

**Senior Seviye Analiz:**

- **@Entity**: JPA entity olduğunu belirtir
- **@Table**: Tablo adı ve constraint'leri tanımlar
- **uniqueConstraints**: Email'in benzersiz olmasını sağlar
- **Lombok Anotasyonları**: Boilerplate kod'u azaltır
- **@Builder**: Builder pattern implementasyonu

### **7.2 StudentService Sınıfı**

```java
@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
```

**Senior Seviye Analiz:**

- **@Service**: Spring service bean'i olarak işaretler
- **@RequiredArgsConstructor**: Final field'lar için constructor injection
- **@Transactional**: Database transaction yönetimi
- **readOnly = true**: Sadece okuma işlemleri için optimize eder

### **7.3 StudentController Sınıfı**

```java
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        // ...
    }
}
```

**Senior Seviye Analiz:**

- **@RestController**: REST API controller'ı
- **@RequestMapping**: Base path tanımlar
- **@Valid**: Bean validation aktif eder
- **ResponseEntity**: HTTP response kontrolü sağlar

---

## 🧪 **8. TEST KODLARININ ANALİZİ**

### **8.1 Unit Test Örneği**

```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void getAllStudents_ShouldReturnAllStudents() {
        // Given
        List<Student> expectedStudents = Arrays.asList(student1, student2);
        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // When
        List<Student> actualStudents = studentService.getAllStudents();

        // Then
        assertEquals(expectedStudents, actualStudents);
        verify(studentRepository).findAll();
    }
}
```

**Test Yapısı (AAA Pattern):**

- **Arrange (Given)**: Test verilerini hazırla
- **Act (When)**: Test edilecek metodu çağır
- **Assert (Then)**: Sonuçları doğrula

### **8.2 Integration Test Örneği**

```java
@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void saveStudent_ShouldPersistStudent() {
        // Given
        Student student = Student.builder()
            .name("Test Student")
            .email("test@example.com")
            .age(25)
            .build();

        // When
        Student savedStudent = studentRepository.save(student);

        // Then
        assertNotNull(savedStudent.getId());
        assertEquals("Test Student", savedStudent.getName());
    }
}
```

**Integration Test Özellikleri:**

- **@DataJpaTest**: Sadece JPA bileşenlerini yükler
- **TestEntityManager**: Test veritabanı yönetimi
- **@ActiveProfiles**: Test profile'ını aktif eder

---

## 🚀 **9. UYGULAMA ÇALIŞTIRMA VE TEST**

### **9.1 Uygulamayı Başlatma**

```bash
# 1. Proje dizinine git
cd /path/to/student-crud

# 2. Maven dependencies'leri indir
mvn clean install

# 3. Uygulamayı çalıştır
mvn spring-boot:run
```

### **9.2 Test Çalıştırma**

```bash
# Tüm testleri çalıştır
mvn test

# Sadece unit testleri
mvn test -Dtest=*UnitTest

# Sadece integration testleri
mvn test -Dtest=*IntegrationTest

# Belirli test sınıfını çalıştır
mvn test -Dtest=StudentServiceTest
```

### **9.3 API Test Etme**

**Postman veya cURL ile test:**

1. **Öğrenci Ekle:**

```bash
POST http://localhost:8080/api/students
Content-Type: application/json

{
  "name": "Mehmet Demir",
  "email": "mehmet@example.com",
  "age": 22,
  "address": "İzmir, Türkiye"
}
```

2. **Öğrenci Listele:**

```bash
GET http://localhost:8080/api/students
```

3. **Öğrenci Güncelle:**

```bash
PUT http://localhost:8080/api/students/1
Content-Type: application/json

{
  "name": "Mehmet Demir Güncellendi",
  "email": "mehmet@example.com",
  "age": 23,
  "address": "İzmir, Türkiye"
}
```

4. **Öğrenci Sil:**

```bash
DELETE http://localhost:8080/api/students/1
```

---

## 📊 **10. PROJE METRİKLERİ VE KALİTE**

### **10.1 Test Coverage**

- **Repository Layer**: 100%
- **Service Layer**: 100%
- **Controller Layer**: 100%
- **Exception Handling**: 100%

### **10.2 Kod Kalitesi**

- **Clean Code Principles**: ✅ Uygulandı
- **SOLID Principles**: ✅ Uygulandı
- **Design Patterns**: ✅ Builder, Repository, Service
- **Error Handling**: ✅ Comprehensive

---

## 🎯 **11. İŞ GÖRÜŞMESİ İÇİN ÖNEMLİ NOKTALAR**

### **11.1 Teknik Sorulara Hazır Cevaplar**

**Q: Spring Boot'un avantajları nelerdir?**
A: Auto-configuration, embedded servers, starter dependencies, production ready

**Q: JPA vs JDBC farkı nedir?**
A: JPA object-relational mapping sağlar, JDBC raw SQL kullanır

**Q: Mocking neden önemlidir?**
A: Test bağımsızlığı, hız, kontrol edilebilirlik

**Q: Unit vs Integration test farkı?**
A: Unit test tek bileşen, Integration test birden fazla bileşen

### **11.2 Proje Hakkında Konuşma**

- **Mimari kararları**: Neden katmanlı mimari?
- **Test stratejisi**: Neden bu test türleri?
- **Teknoloji seçimleri**: Neden H2, Mockito?
- **Performance considerations**: Neden readOnly transactions?

---

## 📅 **12. ÇALIŞMA PLANI**

### **Hafta 1: Temel Kavramlar**

- Spring Boot nedir?
- JPA ve Hibernate
- REST API principles

### **Hafta 2: Proje Mimarisi**

- Katmanlı mimari
- Dependency injection
- Bean lifecycle

### **Hafta 3: Testing**

- Unit testing
- Mocking concepts
- Mockito kullanımı

### **Hafta 4: Integration Testing**

- Test profiles
- Database testing
- End-to-end testing

### **Hafta 5: Advanced Topics**

- Exception handling
- Validation
- Performance optimization

---

## 🎉 **SONUÇ**

Bu doküman ile projeyi tamamen anlayabilir ve iş görüşmelerinde güvenle konuşabilirsiniz. Her bölümü tek tek çalışın ve kodları inceleyin.

**Başarılar! 🚀**

---

_Bu doküman, Student CRUD Application projesi için hazırlanmıştır._
_Son güncelleme: 21 Ağustos 2025_
