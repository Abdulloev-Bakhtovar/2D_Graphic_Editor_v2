# 2D Graphic Editor v2

Описание

Это расширенная версия графического редактора на Java, использующая Swing для создания пользовательского интерфейса. Этот редактор позволяет рисовать, редактировать и удалять линии, а также группировать, перемещать, масштабировать, проецировать и вращать их.
Функциональность

    Создание линий: Пользователи могут рисовать линии на экране.
    Редактирование линий: Выбор и изменение существующих линий.
    Удаление линий: Удаление выбранных линий.
    Группировка линий: Создание групп линий для коллективных операций.
    Перемещение линий: Смещение выбранных линий или групп.
    Масштабирование линий: Изменение размера выбранных линий или групп.
    Проецирование линий: Применение проекций к линиям.
    Вращение линий: Вращение выбранных линий или групп.
    Очистка экрана: Удаление всех линий с экрана.

Компоненты

    GraphicEditor: Главный класс, отвечающий за создание интерфейса и управление взаимодействиями.
    DrawingPanel: Панель для рисования, обрабатывает события мыши и отображает линии.
    Line: Класс, представляющий линию.

Использование

Запустите приложение, выполнив метод main в классе GraphicEditor. Интерфейс предоставляет кнопки для выбора режимов и панель для рисования.
Управление

    Клавиши: 'S' для масштабирования, 'P' для проецирования, 'R' для вращения, '+' и '-' для изменения масштаба.
    Мышь: Используйте клики и перемещение мыши для рисования и редактирования линий.

Зависимости

    Java SE Development Kit (JDK)
    Swing (часть JDK)

Как запустить

    Убедитесь в наличии JDK.
    Скомпилируйте код: javac GraphicEditor.java.
    Запустите приложение: java GraphicEditor.

Лицензия

Проект распространяется на условиях лицензии MIT.
