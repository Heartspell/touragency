(function () {
  const KEY = 'tkg.lang';

  const ru = {
    'nav.home': 'Главная',
    'nav.tours': 'Туры',
    'nav.operator': 'Кабинет оператора',
    'nav.admin': 'Админ',
    'nav.login': 'Войти',
    'nav.logout': 'Выйти',
    'nav.register': 'Регистрация',

    'auth.welcome': 'Добро пожаловать в TourKG',
    'auth.subtitle': 'Войдите или создайте аккаунт',
    'auth.tab.login': 'Вход',
    'auth.tab.register': 'Регистрация',
    'auth.email': 'Email',
    'auth.password': 'Пароль',
    'auth.fullName': 'Полное имя',
    'auth.phone': 'Телефон',
    'auth.passMin': 'Минимум 6 символов',
    'auth.loginBtn': 'Войти',
    'auth.registerBtn': 'Создать аккаунт',
    'auth.testHint': 'Тест: tourist@tour.kg / tourist123',
    'auth.loggingIn': 'Входим...',
    'auth.registering': 'Регистрируем...',
    'auth.invalidLogin': 'Неверный email или пароль',
    'auth.connectionErrorRetry': 'Ошибка соединения. Попробуйте ещё раз.',
    'auth.connectionError': 'Ошибка соединения',
    'auth.registerSuccess': 'Аккаунт создан! Теперь войдите.',
    'auth.registerError': 'Ошибка регистрации',

    'hero.searchDest': 'Куда летим?',
    'hero.category': 'Категория',
    'hero.budget': 'Бюджет (сом)',
    'hero.searchBtn': 'Найти туры',
    'hero.btnSecondary': 'Смотреть все туры',

    'cat.beach': 'Пляж',
    'cat.mountain': 'Горы',
    'cat.city': 'Город',
    'cat.adventure': 'Приключения',
    'cat.cultural': 'Культура',
    'cat.all': 'Все туры',

    'home.statsTours': 'Туров',
    'home.statsClients': 'Зарегистрированных туристов',
    'home.featuredSubtitle': 'Лучшие туры по отзывам и бронированиям',
    'home.categoriesTitle': 'Выбери по интересам',
    'home.whyTitle': 'Почему выбирают TourKG?',
    'home.safeTitle': 'Безопасно',
    'home.safeText': 'Все операторы проверены. Ваши данные защищены.',
    'home.fastTitle': 'Мгновенно',
    'home.fastText': 'Подтверждение бронирования приходит сразу.',
    'home.localPayTitle': 'Местная оплата',
    'home.localPayText': 'MBank, ElCard и банковские карты КР.',
    'home.mobileTitle': 'На любом устройстве',
    'home.mobileText': 'Сайт отлично работает на смартфоне и ПК.',

    'tours.title': 'Каталог туров',
    'tours.found': 'Найдено туров:',
    'tours.filter': 'Фильтры',
    'tours.destination': 'Направление',
    'tours.country': 'Страна',
    'tours.allCategories': 'Все категории',
    'tours.priceFrom': 'Цена от (сом)',
    'tours.priceTo': 'Цена до (сом)',
    'tours.apply': 'Применить',
    'tours.reset': 'Сбросить',
    'tours.noResultsLong': 'По вашему запросу туров не найдено. Попробуйте изменить фильтры.',
    'tours.perPerson': 'за человека',

    'tour.days': 'дн.',
    'tour.price': 'Цена',
    'tour.book': 'Забронировать',
    'tour.viewDetails': 'Подробнее',
    'tour.reviews': 'отз.',

    'detail.about': 'О туре',
    'detail.dates': 'Даты отправления',
    'detail.options': 'вариантов',
    'detail.noDatesLong': 'Нет доступных дат. Следите за обновлениями.',
    'detail.departure': 'Отправление',
    'detail.return': 'Возвращение',
    'detail.seatsShort': 'мест',
    'detail.left': 'Осталось',
    'detail.noSeats': 'Нет мест',
    'detail.loginToBook': 'Войти для брони',
    'detail.busy': 'Занято',
    'detail.reviews': 'Отзывы',
    'detail.noReviews': 'Отзывов пока нет. Будьте первым!',
    'detail.perOnePerson': 'за 1 человека',
    'detail.instantConfirm': 'Мгновенное подтверждение',
    'detail.safePay': 'Безопасная оплата',
    'detail.support': 'Поддержка 24/7',
    'detail.freeCancel': 'Бесплатная отмена',
    'detail.bookNow': 'Забронировать сейчас',
    'detail.loginAndBook': 'Войти и забронировать',
    'detail.noDates': 'Нет доступных дат',
    'detail.operator': 'Оператор',

    'cab.myBookings': 'Мои бронирования',
    'cab.tourist': 'Турист',
    'cab.totalBook': 'Всего броней:',
    'cab.findTours': 'Найти туры',
    'cab.noBookings': 'У вас пока нет бронирований',
    'cab.openCatalog': 'Откройте каталог и выберите свой первый тур!',
    'cab.seeTours': 'Смотреть туры',

    'footer.description': 'Первая платформа онлайн-бронирования туров, созданная специально для Кыргызстана.',
    'footer.beachTours': 'Пляжный отдых',
    'footer.mountainTours': 'Горный туризм',
    'footer.culturalTours': 'Культурные туры',
    'footer.kyrgyztours': 'Туры по КР',
    'footer.company': 'Компания',
    'footer.about': 'О нас',
    'footer.contact': 'Контакты',
    'footer.copyright': '© 2026 TourKG. Все права защищены.',

    'misc.people': 'чел.',
    'misc.back': 'Назад'
  };

  const kg = {
    'nav.home': 'Башкы',
    'nav.tours': 'Турлар',
    'nav.operator': 'Оператор кабинети',
    'nav.admin': 'Админ',
    'nav.login': 'Кирүү',
    'nav.logout': 'Чыгуу',
    'nav.register': 'Катталуу',

    'auth.welcome': 'TourKGге кош келиңиз',
    'auth.subtitle': 'Кириңиз же аккаунт түзүңүз',
    'auth.tab.login': 'Кирүү',
    'auth.tab.register': 'Катталуу',
    'auth.email': 'Электрондук почта',
    'auth.password': 'Сыр сөз',
    'auth.fullName': 'Толук аты-жөнү',
    'auth.phone': 'Телефон',
    'auth.passMin': 'Кеминде 6 белги',
    'auth.loginBtn': 'Кирүү',
    'auth.registerBtn': 'Аккаунт түзүү',
    'auth.testHint': 'Тест: tourist@tour.kg / tourist123',
    'auth.loggingIn': 'Кирүүдө...',
    'auth.registering': 'Катталууда...',
    'auth.invalidLogin': 'Email же сыр сөз туура эмес',
    'auth.connectionErrorRetry': 'Туташууда ката кетти. Кайра аракет кылыңыз.',
    'auth.connectionError': 'Туташууда ката кетти',
    'auth.registerSuccess': 'Аккаунт түзүлдү! Эми кириңиз.',
    'auth.registerError': 'Катталууда ката кетти',

    'hero.searchDest': 'Кайда барасыз?',
    'hero.category': 'Категория',
    'hero.budget': 'Бюджет (сом)',
    'hero.searchBtn': 'Турларды табуу',
    'hero.btnSecondary': 'Бардык турларды көрүү',

    'cat.beach': 'Жээк',
    'cat.mountain': 'Тоолор',
    'cat.city': 'Шаар',
    'cat.adventure': 'Активдүү эс алуу',
    'cat.cultural': 'Маданият',
    'cat.all': 'Бардык турлар',

    'home.statsTours': 'Турлар',
    'home.statsClients': 'Катталган туристтер',
    'home.featuredSubtitle': 'Пикирлер жана брондоолор боюнча мыкты турлар',
    'home.categoriesTitle': 'Кызыгууңуз боюнча тандаңыз',
    'home.whyTitle': 'Эмне үчүн TourKG тандашат?',
    'home.safeTitle': 'Коопсуз',
    'home.safeText': 'Бардык операторлор текшерилген. Маалыматыңыз корголгон.',
    'home.fastTitle': 'Ылдам',
    'home.fastText': 'Брондоо тастыктоосу дароо келет.',
    'home.localPayTitle': 'Жергиликтүү төлөм',
    'home.localPayText': 'MBank, ElCard жана Кыргызстандын банк карталары.',
    'home.mobileTitle': 'Каалаган түзмөктө',
    'home.mobileText': 'Сайт смартфондо да, компьютерде да ыңгайлуу иштейт.',

    'tours.title': 'Турлардын каталогу',
    'tours.found': 'Табылган турлар:',
    'tours.filter': 'Чыпкалар',
    'tours.destination': 'Багыт',
    'tours.country': 'Өлкө',
    'tours.allCategories': 'Бардык категориялар',
    'tours.priceFrom': 'Баасы баштап (сом)',
    'tours.priceTo': 'Баасы чейин (сом)',
    'tours.apply': 'Колдонуу',
    'tours.reset': 'Тазалоо',
    'tours.noResultsLong': 'Сурамыңыз боюнча турлар табылган жок. Чыпкаларды өзгөртүп көрүңүз.',
    'tours.perPerson': 'бир адамга',

    'tour.days': 'күн',
    'tour.price': 'Баасы',
    'tour.book': 'Брондоо',
    'tour.viewDetails': 'Толугураак',
    'tour.reviews': 'пикир',

    'detail.about': 'Тур жөнүндө',
    'detail.dates': 'Жөнөө күндөрү',
    'detail.options': 'вариант',
    'detail.noDatesLong': 'Жеткиликтүү күндөр жок. Жаңыртууларды күтүңүз.',
    'detail.departure': 'Жөнөө',
    'detail.return': 'Кайтуу',
    'detail.seatsShort': 'орун',
    'detail.left': 'Калды',
    'detail.noSeats': 'Орун жок',
    'detail.loginToBook': 'Брондоо үчүн кириңиз',
    'detail.busy': 'Бош эмес',
    'detail.reviews': 'Пикирлер',
    'detail.noReviews': 'Азырынча пикир жок. Биринчи болуңуз!',
    'detail.perOnePerson': '1 адамга',
    'detail.instantConfirm': 'Дароо тастыктоо',
    'detail.safePay': 'Коопсуз төлөм',
    'detail.support': '24/7 колдоо',
    'detail.freeCancel': 'Акысыз жокко чыгаруу',
    'detail.bookNow': 'Азыр брондоо',
    'detail.loginAndBook': 'Кирип брондоо',
    'detail.noDates': 'Жеткиликтүү күндөр жок',
    'detail.operator': 'Оператор',

    'cab.myBookings': 'Менин брондоолорум',
    'cab.tourist': 'Турист',
    'cab.totalBook': 'Жалпы брондоолор:',
    'cab.findTours': 'Турларды табуу',
    'cab.noBookings': 'Азырынча брондооңуз жок',
    'cab.openCatalog': 'Каталогду ачып, биринчи туруңузду тандаңыз!',
    'cab.seeTours': 'Турларды көрүү',

    'footer.description': 'Кыргызстан үчүн түзүлгөн турларды онлайн брондоо платформасы.',
    'footer.beachTours': 'Жээктеги эс алуу',
    'footer.mountainTours': 'Тоо туризми',
    'footer.culturalTours': 'Маданий турлар',
    'footer.kyrgyztours': 'Кыргызстан боюнча турлар',
    'footer.company': 'Компания',
    'footer.about': 'Биз жөнүндө',
    'footer.contact': 'Байланыш',
    'footer.copyright': '© 2026 TourKG. Бардык укуктар корголгон.',

    'misc.people': 'адам',
    'misc.back': 'Артка'
  };

  const dict = { ru, kg };

  function getLang() {
    return localStorage.getItem(KEY) || 'ru';
  }

  function translate(key, lang) {
    return (dict[lang] && dict[lang][key]) || ru[key] || null;
  }

  function setText(lang) {
    document.querySelectorAll('[data-i18n]').forEach(function (el) {
      const text = translate(el.getAttribute('data-i18n'), lang);
      if (text) el.textContent = text;
    });

    document.querySelectorAll('[data-i18n-placeholder]').forEach(function (el) {
      const text = translate(el.getAttribute('data-i18n-placeholder'), lang);
      if (text) el.setAttribute('placeholder', text);
    });
  }

  function setLang(lang) {
    localStorage.setItem(KEY, lang);
    document.cookie = 'tkg-lang=' + (lang === 'kg' ? 'ky' : 'ru') + '; path=/; max-age=31536000';
    document.documentElement.lang = lang === 'kg' ? 'ky' : 'ru';
    setText(lang);
    document.querySelectorAll('[data-lang-btn]').forEach(function (btn) {
      btn.classList.toggle('active', btn.getAttribute('data-lang-btn') === lang);
    });
  }

  window.TKG_I18N = { getLang, setLang, translate };

  document.addEventListener('DOMContentLoaded', function () {
    setLang(getLang());
  });
})();
