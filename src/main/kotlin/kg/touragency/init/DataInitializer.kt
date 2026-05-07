package kg.touragency.init

import kg.touragency.entity.*
import kg.touragency.repository.*
import kg.touragency.service.SiteSettingsService
import kg.touragency.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class DataInitializer(
    private val userService: UserService,
    private val tourRepository: TourRepository,
    private val tourDateRepository: TourDateRepository,
    private val bookingRepository: BookingRepository,
    private val reviewRepository: ReviewRepository,
    private val siteSettingsService: SiteSettingsService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Настройки главной страницы по умолчанию.
        val defaults = mapOf(
            "hero_title" to "Откройте Кыргызстан и мир",
            "hero_subtitle" to "Лучшие туры от проверенных операторов. Бронируйте онлайн — быстро, безопасно, выгодно.",
            "hero_badge" to "✈ Более 100 направлений",
            "hero_btn_primary" to "Найти тур",
            "hero_btn_secondary" to "Смотреть все туры",
            "stats_tours" to "500+",
            "stats_clients" to "12 000+",
            "stats_years" to "8",
            "stats_rating" to "4.9",
            "featured_section_title" to "Популярные направления",
            "footer_phone" to "+996 312 000 000",
            "footer_email" to "info@tourkg.com",
            "footer_address" to "г. Бишкек, пр. Чуй 155"
        )
        for ((key, value) in defaults) {
            val oldValue = siteSettingsService.get(key)
            if (oldValue == "") {
                siteSettingsService.set(key, value)
            }
        }

        // Если админ уже есть, тестовые данные повторно не создаем.
        if (userService.findByEmail("admin@tour.kg") != null) {
            return
        }

        val admin = userService.register("admin@tour.kg", "admin123", "Администратор", "+996700000001", UserRole.ADMIN)
        val operator = userService.register("operator@tour.kg", "operator123", "Туристик Оператор", "+996700000002", UserRole.OPERATOR)
        val tourist = userService.register("tourist@tour.kg", "tourist123", "Иван Туристов", "+996700000003", UserRole.TOURIST)

        data class TourInfo(
            val title: String,
            val description: String,
            val destination: String,
            val country: String,
            val category: TourCategory,
            val days: Int,
            val price: BigDecimal
        )

        val toursData = listOf(
            // Кыргызстан.
            TourInfo(
                "Иссык-Куль — Жемчужина Средней Азии",
                "Отдых на берегу уникального высокогорного озера Иссык-Куль — одного из крупнейших горных озёр мира. Кристально чистая вода, живописные пляжи у подножия снежных пиков Тянь-Шаня, горячие источники и свежий горный воздух. Программа включает купание, прогулки по прибрежным сёлам, посещение петроглифов Чолпон-Ата и дегустацию традиционной кыргызской кухни.",
                "Иссык-Куль", "Кыргызстан", TourCategory.BEACH, 3, BigDecimal("8500")
            ),
            TourInfo(
                "Ала-Тоо — Треккинг в Тянь-Шане",
                "Захватывающий поход по горным тропам Национального парка Ала-Арча. Маршрут пролегает через альпийские луга, ледники и горные озёра на высоте от 1600 до 3700 метров. Ночёвки в юртах местных пастухов-чабанов, дегустация кумыса и бешбармака. Идеально для любителей активного отдыха и настоящей дикой природы.",
                "Ала-Тоо", "Кыргызстан", TourCategory.MOUNTAIN, 5, BigDecimal("12000")
            ),
            TourInfo(
                "Каракол — Город у ледников",
                "Экспедиция в восточную часть Иссык-Куля. Посещение ущелья Барскаун с водопадами, горячие источники Алтын-Арашан, треккинг к леднику Ак-Суу. Прогулка по историческому центру Каракола, посещение дунганской мечети и православного собора. Конные прогулки по предгорьям.",
                "Каракол", "Кыргызстан", TourCategory.ADVENTURE, 4, BigDecimal("9800")
            ),
            TourInfo(
                "Сон-Куль — Высокогорное кочевье",
                "Уникальный тур к священному озеру Сон-Куль на высоте 3016 метров. Путешествие верхом или на джипах через горные перевалы. Проживание в традиционных юртах прямо на берегу озера. Наблюдение за закатами и рассветами, рыбалка, участие в кочевом быту. Настоящий опыт кыргызской кочевой культуры.",
                "Сон-Куль", "Кыргызстан", TourCategory.CULTURAL, 3, BigDecimal("11000")
            ),
            TourInfo(
                "Ош — Древний Шёлковый путь",
                "Погружение в историю одного из старейших городов Средней Азии. Подъём на священную гору Сулейман-Тоо (объект ЮНЕСКО), прогулка по крупнейшему базару Центральной Азии — рынку Джайма. Экскурсия по Ошской долине, дегустация плова и самсы. Посещение мечети Рабат Абдулхан и Краеведческого музея.",
                "Ош", "Кыргызстан", TourCategory.CULTURAL, 3, BigDecimal("7500")
            ),

            // Турция.
            TourInfo(
                "Турция — Анталья All Inclusive",
                "Пляжный отдых на Средиземноморском побережье Турции. Отели 5* с системой «всё включено» — безлимитная еда, напитки, развлечения. Анимация для детей и взрослых, аквапарки, дайвинг и снорклинг в бирюзовых водах. Экскурсии в античный Перге, Термес и Аспендос с амфитеатром II века н.э.",
                "Анталья", "Турция", TourCategory.BEACH, 7, BigDecimal("45000")
            ),
            TourInfo(
                "Турция — Каппадокия",
                "Сказочный регион с уникальными природными образованиями — «каменными грибами» и подземными городами. Полёт на воздушном шаре над долиной на рассвете — незабываемые впечатления на всю жизнь. Посещение подземного города Деринкую, долины Гёреме, пещерных церквей. Дегустация турецкой кухни и шопинг на рынке.",
                "Каппадокия", "Турция", TourCategory.CULTURAL, 5, BigDecimal("52000")
            ),

            // ОАЭ.
            TourInfo(
                "ОАЭ — Дубай",
                "Роскошный отдых в городе будущего. Смотровая площадка Бурдж-Халифа — самого высокого здания в мире, шопинг в Mall of the Emirates с горнолыжным склопом внутри торгового центра, сафари в пустыне на джипах с ужином у костра. Пляжный отдых на Jumeirah Beach, круиз на яхте с видом на Dubai Marina.",
                "Дубай", "ОАЭ", TourCategory.CITY, 5, BigDecimal("65000")
            ),

            // Таиланд.
            TourInfo(
                "Таиланд — Пхукет",
                "Экзотический отдых на тропическом острове в Андаманском море. Белоснежные пляжи Патонг, Ката и Карон, кристально чистая вода, кораллы и разноцветные рыбки. Экскурсия на острова Пхи-Пхи — декорации к фильму «Пляж». Тайский массаж, тайская кухня, вечерний тайский бокс. Поездка в буддийские храмы.",
                "Пхукет", "Таиланд", TourCategory.BEACH, 10, BigDecimal("78000")
            ),
            TourInfo(
                "Таиланд — Бангкок и острова",
                "Комбинированный тур: три дня в мегаполисе и неделя на островах. Большой дворец и Изумрудный Будда, ночные рынки и street food, тук-тук по каналам. Затем — переезд на острова Самуи или Панган с белыми пляжами и закатами. Активности: слоны, тайская кулинарная школа, дайвинг.",
                "Бангкок", "Таиланд", TourCategory.CULTURAL, 10, BigDecimal("82000")
            ),

            // Италия.
            TourInfo(
                "Италия — Рим и Ватикан",
                "Культурный тур по вечному городу. Колизей и Форум — сердце Римской империи, Ватиканские музеи и Сикстинская капелла с фресками Микеланджело, фонтан Треви и площадь Навона. Дегустация пасты карбонара, пиццы и джелато. Поездки во Флоренцию и к холмам Тосканы.",
                "Рим", "Италия", TourCategory.CULTURAL, 8, BigDecimal("95000")
            ),

            // Египет.
            TourInfo(
                "Египет — Шарм-эш-Шейх",
                "Коралловые рифы Красного моря — один из лучших дайвинг-спотов планеты. Отели 5* на берегу моря, кристально чистая вода с видимостью до 30 метров, дельфины и черепахи. Экскурсии: монастырь Святой Екатерины на Синае, Каир с пирамидами Гизы и Сфинксом, Луксор с Долиной Царей.",
                "Шарм-эш-Шейх", "Египет", TourCategory.BEACH, 7, BigDecimal("52000")
            ),

            // Китай.
            TourInfo(
                "Китай — Пекин и Великая стена",
                "Великая китайская стена — одно из семи чудес света, протяжённостью более 21 000 км. Запретный город — дворец 600 лет истории и 9999 комнат. Храм Неба, рынок Шелка, Олимпийский парк. Дегустация пекинской утки. Поездка в Сиань к Терракотовой армии — 8000 воинов в полный рост.",
                "Пекин", "Китай", TourCategory.CULTURAL, 8, BigDecimal("88000")
            ),

            // Грузия.
            TourInfo(
                "Грузия — Тбилиси и горы",
                "Страна Золотого руна и колыбель виноделия. Старый Тбилиси с сернистыми банями, крепостью Нарикала и балконами, увитыми виноградом. Военно-Грузинская дорога к горе Казбеги и церкви Гергети в облаках. Кахетия — столица грузинского вина, дегустация в маранях. Монастырь Давид-Гареджи.",
                "Тбилиси", "Грузия", TourCategory.CULTURAL, 7, BigDecimal("38000")
            ),

            // Бали.
            TourInfo(
                "Бали — Остров богов",
                "Тропический рай на индонезийском острове. Рисовые террасы Тегалалланг, индуистский храм Танах Лот над морем, священная обезьяна лес в Убуде. Серфинг на Куте, йога-ретриты в Убуде, балийский массаж и спа. Вулкан Батур — восход над облаками. Рынки с местными специями и серебром.",
                "Убуд / Кута", "Индонезия", TourCategory.BEACH, 12, BigDecimal("92000")
            ),

            // Япония.
            TourInfo(
                "Япония — Токио и Киото",
                "Страна восходящего солнца — уникальное сочетание традиций и технологий. Токио: Сибуя, Акихабара, Асакуса с храмом Сенсо-дзи. Чайная церемония и урок икебаны в Киото. Бамбуковая роща Арасияма, золотой павильон Кинкаку-дзи. Фудзияма и онсэн. Суши и рамэн в местных ресторанах. Покупки в Акихабаре.",
                "Токио / Киото", "Япония", TourCategory.CULTURAL, 14, BigDecimal("145000")
            )
        )

        val now = LocalDate.now()
        val tours = mutableListOf<Tour>()

        // Создаем туры и сразу добавляем даты выезда.
        for (info in toursData) {
            val tour = Tour(
                title = info.title, description = info.description,
                destination = info.destination, country = info.country,
                durationDays = info.days, price = info.price,
                category = info.category, status = TourStatus.ACTIVE,
                operator = operator
            )
            val saved = tourRepository.save(tour)

            tourDateRepository.save(TourDate(tour = saved, departureDate = now.plusMonths(1), returnDate = now.plusMonths(1).plusDays(info.days.toLong()), totalSeats = 20))
            tourDateRepository.save(TourDate(tour = saved, departureDate = now.plusMonths(2), returnDate = now.plusMonths(2).plusDays(info.days.toLong()), totalSeats = 20))
            tourDateRepository.save(TourDate(tour = saved, departureDate = now.plusMonths(3), returnDate = now.plusMonths(3).plusDays(info.days.toLong()), totalSeats = 15))
            tours.add(saved)
        }

        // Примеры бронирований.
        val firstDate = tourDateRepository.findByTour(tours[0]).first()
        firstDate.bookedSeats = 2
        tourDateRepository.save(firstDate)
        bookingRepository.save(Booking(tourist = tourist, tourDate = firstDate, participants = 2,
            totalPrice = tours[0].price.multiply(BigDecimal("2")), status = BookingStatus.CONFIRMED))

        val secondDate = tourDateRepository.findByTour(tours[4]).first()
        secondDate.bookedSeats = 1
        tourDateRepository.save(secondDate)
        bookingRepository.save(Booking(tourist = tourist, tourDate = secondDate, participants = 1,
            totalPrice = tours[4].price, status = BookingStatus.CONFIRMED))

        // Примеры отзывов.
        val review1 = Review(tourist = tourist, tour = tours[0], rating = 5,
            comment = "Отличный тур! Озеро Иссык-Куль просто потрясающее. Вода чистейшая, виды на горы невероятные. Обязательно поедем ещё раз!")
        reviewRepository.save(review1)
        tours[0].rating = 5.0
        tours[0].reviewCount = 1
        tourRepository.save(tours[0])

        val review2 = Review(tourist = tourist, tour = tours[5], rating = 5,
            comment = "Турция превзошла все ожидания! Отель 5 звёзд, еда восхитительная, море тёплое. Всё включено — это лучший формат отдыха.")
        reviewRepository.save(review2)
        tours[5].rating = 5.0
        tours[5].reviewCount = 1
        tourRepository.save(tours[5])

        val review3 = Review(tourist = tourist, tour = tours[7], rating = 4,
            comment = "Дубай — город мечты! Бурдж-Халифа впечатляет, пустынное сафари незабываемо. Немного дорого, но оно того стоит.")
        reviewRepository.save(review3)
        tours[7].rating = 4.0
        tours[7].reviewCount = 1
        tourRepository.save(tours[7])
    }
}
