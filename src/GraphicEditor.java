import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Главный класс графического редактора
public class GraphicEditor {

    private JFrame frame; // Основное окно программы
    private DrawingPanel drawingPanel; // Панель для рисования
    private JButton createMode, editMode, deleteMode, groupMode, moveMode, scaleMode, projectMode, rotateMode, clearScreenButton; // Кнопки режимов редактора
    private JLabel statusBar; // Статусная строка

    // Перечисление для режимов редактора: создание, редактирование, удаление, группировка, смещение, масштабирование, проецирование, вращение, без действия
    public enum Mode {
        CREATE, EDIT, DELETE, GROUP, MOVE, SCALE, PROJECT, ROTATE, NONE
    }

    private Mode currentMode = Mode.NONE; // Текущий режим редактора

    public GraphicEditor() {
        frame = new JFrame("2D Graphic Editor v2"); // Создание основного окна

        drawingPanel = new DrawingPanel(); // Инициализация панели рисования

        createMode = new JButton("Создать");
        editMode = new JButton("Редактировать");
        deleteMode = new JButton("Удалить");
        groupMode = new JButton("Группировать");
        moveMode = new JButton("Сместить");
        scaleMode = new JButton("Масштабировать");
        projectMode = new JButton("Проецировать");
        rotateMode = new JButton("Вращать");
        clearScreenButton = new JButton("Очистить экран");

        statusBar = new JLabel("Статусная строка");

        // Установка режимов при нажатии на кнопки
        createMode.addActionListener(e -> currentMode = Mode.CREATE);
        editMode.addActionListener(e -> currentMode = Mode.EDIT);
        deleteMode.addActionListener(e -> currentMode = Mode.DELETE);
        groupMode.addActionListener(e -> currentMode = Mode.GROUP);
        moveMode.addActionListener(e -> currentMode = Mode.MOVE);
        scaleMode.addActionListener(e -> currentMode = Mode.SCALE);
        projectMode.addActionListener(e -> currentMode = Mode.PROJECT);
        rotateMode.addActionListener(e -> currentMode = Mode.ROTATE);

        clearScreenButton.addActionListener(e -> {
            currentMode = Mode.NONE; // Устанавливаем режим без действия
            drawingPanel.clearScreen(); // Очищаем экран на панели рисования
        });

        // Панель с кнопками режимов
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createMode);
        buttonPanel.add(editMode);
        buttonPanel.add(deleteMode);
        buttonPanel.add(groupMode);
        buttonPanel.add(moveMode);
        buttonPanel.add(scaleMode);
        buttonPanel.add(projectMode);
        buttonPanel.add(rotateMode);
        buttonPanel.add(clearScreenButton); // Добавляем кнопку "Очистить экран"

        // Добавление компонентов в главное окно
        frame.setLayout(new BorderLayout());
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(drawingPanel, BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);

        // Настройки окна
        frame.setSize(1460,860);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Добавим обработчик событий для клавиатуры
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_S || e.getKeyChar() == '+') {
                    // Нажата клавиша 'S' - выполнить операцию масштабирования
                    double scaleFactor = 2.0; // Пример масштабирующего коэффициента
                    drawingPanel.scaleSelectedLines(scaleFactor);
                } else if (keyCode == KeyEvent.VK_P) {
                    // Нажата клавиша 'P' - выполнить операцию проецирования
                    double xFactor = 0.5; // Пример коэффициента проецирования по X
                    double yFactor = 1.5; // Пример коэффициента проецирования по Y
                    drawingPanel.projectSelectedLines(xFactor, yFactor);
                } else if (keyCode == KeyEvent.VK_R) {
                    // Нажата клавиша 'R' - выполнить операцию вращения
                    double angleInRadians = Math.toRadians(1); // Пример угла в радианах
                    drawingPanel.rotateSelectedLines(angleInRadians);
                }else if (e.getKeyChar() == '-') {
                    // Вызываем метод для уменьшения масштаба выбранных линий
                    drawingPanel.decreaseScaleSelectedLines(2.0); // Примерный коэффициент уменьшения
                }
            }
        });

        // Устанавливаем фокус для обработки клавиш клавиатуры
        frame.setFocusable(true);
        frame.requestFocus();
    }

    // Панель для рисования линий
    class DrawingPanel extends JPanel {
        private ArrayList<Line> lines = new ArrayList<>(); // Список всех линий
        private Line currentLine; // Текущая линия
        private Line selectedLine; // Выбранная линия для редактирования
        private ArrayList<Line> selectedLines = new ArrayList<>(); // Список выбранных линий для группировки
        private Point startPoint;// Объявляем startPoint как поле

        public DrawingPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (currentMode == Mode.CREATE) {
                        // Режим создания: начало и конец линии
                        if (currentLine == null) {
                            currentLine = new Line(e.getPoint(), e.getPoint());
                        } else {
                            currentLine.setEndPoint(e.getPoint());
                            lines.add(currentLine);
                            currentLine = null;
                        }
                    } else if (currentMode == Mode.EDIT) {
                            // Режим редактирования: выбор или отмена выбора линии
                            if (selectedLine == null) {
                                selectedLine = findNearestLine(e.getPoint());
                            } else {
                                selectedLine = null;
                            }
                    } else if (currentMode == Mode.GROUP) {
                        // Режим группировки: выбор или отмена выбора линий
                        Line clickedLine = findNearestLine(e.getPoint());
                        if (clickedLine != null) {
                            if (selectedLines.contains(clickedLine)) {
                                selectedLines.remove(clickedLine);
                            } else {
                                selectedLines.add(clickedLine);
                            }
                            repaint();
                        }
                    }else if (currentMode == Mode.DELETE) {
                        // Режим удаления: удаление ближайшей линии
                        Line lineToDelete = findNearestLine(e.getPoint());
                        if (lineToDelete != null) {
                            lines.remove(lineToDelete);
                            repaint();
                        }
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (currentMode == Mode.CREATE && currentLine != null) {
                        currentLine.setEndPoint(e.getPoint());
                        repaint();
                    } else if (currentMode == Mode.EDIT && selectedLine != null) {
                        selectedLine.setEndPoint(e.getPoint());
                        repaint();
                        // Отображение информации о выбранной линии
                        statusBar.setText("Начало: [x=" + selectedLine.getStartPoint().x + " y=" + selectedLine.getStartPoint().y + "], " +
                                "Конец: [x=" + selectedLine.getEndPoint().x + " y=" + selectedLine.getEndPoint().y + "], " +
                                "Длина: " + String.format("%.2f", selectedLine.getLength()));
                        return;
                    }

                    Line nearestLine = findNearestLine(e.getPoint());
                    if (nearestLine != null) {
                        statusBar.setText("Уравнение линии: " + e.getX() + " * x + " + e.getY() + " * y + C = 0");
                    } else {
                        statusBar.setText("Точка: (" + e.getX() + ", " + e.getY() + ")");
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                // Обработчик нажатия кнопки мыши
                @Override
                public void mousePressed(MouseEvent e) {
                    if (currentMode == Mode.MOVE && selectedLines.size() > 0) {
                        // Запоминаем начальные координаты мыши
                        startPoint = e.getPoint();
                    }
                }

                // Обработчик отпускания кнопки мыши
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (currentMode == Mode.MOVE && startPoint != null) {
                        // Вычисляем смещение
                        int deltaX = e.getPoint().x - startPoint.x;
                        int deltaY = e.getPoint().y - startPoint.y;

                        // Смещаем выбранные линии
                        moveSelectedLines(deltaX, deltaY);

                        // Сбрасываем начальные координаты
                        startPoint = null;
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                // Обработчик перемещения мыши
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (currentMode == Mode.MOVE && startPoint != null) {
                        // Ничего не делаем в этом методе, обработка происходит при отпускании кнопки мыши
                    }
                }
            });
        }

        public void clearScreen() {
            currentMode = Mode.NONE; // Устанавливаем режим без действия
            lines.clear(); // Очищаем список линий
            selectedLines.clear(); // Очищаем список выбранных линий
            repaint(); // Перерисовываем панель
        }

        // Добавить метод для смещения выбранных линий
        public void moveSelectedLines(int deltaX, int deltaY) {
            for (Line line : selectedLines) {
                Point startPoint = line.getStartPoint();
                Point endPoint = line.getEndPoint();
                startPoint.translate(deltaX, deltaY);
                endPoint.translate(deltaX, deltaY);
            }
            repaint();
        }

        // Добавить метод для масштабирования выбранных линий
        public void scaleSelectedLines(double scaleFactor) {
            for (Line line : selectedLines) {
                Point startPoint = line.getStartPoint();
                Point endPoint = line.getEndPoint();
                startPoint.setLocation(startPoint.getX() * scaleFactor, startPoint.getY() * scaleFactor);
                endPoint.setLocation(endPoint.getX() * scaleFactor, endPoint.getY() * scaleFactor);
            }
            repaint();
        }

        // Добавить метод для уменьшение масштаб выбранных линий
        public void decreaseScaleSelectedLines(double scaleFactor) {
            for (Line line : selectedLines) {
                Point startPoint = line.getStartPoint();
                Point endPoint = line.getEndPoint();

                // Уменьшение масштаба линии
                startPoint.setLocation(startPoint.getX() / scaleFactor, startPoint.getY() / scaleFactor);
                endPoint.setLocation(endPoint.getX() / scaleFactor, endPoint.getY() / scaleFactor);
            }
            repaint();
        }


        // Добавить метод для проецирования выбранных линий
        public void projectSelectedLines(double xFactor, double yFactor) {
            for (Line line : selectedLines) {
                Point startPoint = line.getStartPoint();
                Point endPoint = line.getEndPoint();
                startPoint.setLocation(startPoint.getX() * xFactor, startPoint.getY() * yFactor);
                endPoint.setLocation(endPoint.getX() * xFactor, endPoint.getY() * yFactor);
            }
            repaint();
        }

        // Добавить метод для вращения выбранных линий
        public void rotateSelectedLines(double angleInRadians) {
            double cos = Math.cos(angleInRadians);
            double sin = Math.sin(angleInRadians);
            for (Line line : selectedLines) {
                Point startPoint = line.getStartPoint();
                Point endPoint = line.getEndPoint();
                double startX = startPoint.getX();
                double startY = startPoint.getY();
                double endX = endPoint.getX();
                double endY = endPoint.getY();
                startPoint.setLocation(startX * cos - startY * sin, startX * sin + startY * cos);
                endPoint.setLocation(endX * cos - endY * sin, endX * sin + endY * cos);
            }
            repaint();
        }

        // Найти ближайшую линию к заданной точке
        private Line findNearestLine(Point p) {
            Line nearestLine = null;
            double minimumDistance = Double.MAX_VALUE;

            for (Line line : lines) {
                double distance = distanceToSegment(line.getStartPoint(), line.getEndPoint(), p);
                if (distance < minimumDistance) {
                    minimumDistance = distance;
                    nearestLine = line;
                }
            }

            if (minimumDistance < 10) {
                return nearestLine;
            } else {
                return null;
            }
        }

        // Вычисление расстояния от точки до отрезка
        private double distanceToSegment(Point p1, Point p2, Point p) {
            double norm = Math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
            return Math.abs((p.x - p1.x) * (p2.y - p1.y) - (p.y - p1.y) * (p2.x - p1.x)) / norm;
        }

        // Отображение всех линий на панели
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Line line : lines) {
                g.setColor(line == selectedLine ? Color.RED : Color.BLACK);
                g.drawLine(line.getStartPoint().x, line.getStartPoint().y, line.getEndPoint().x, line.getEndPoint().y);
            }

            for (Line selected : selectedLines) {
                g.setColor(Color.BLUE);
                g.drawLine(selected.getStartPoint().x, selected.getStartPoint().y, selected.getEndPoint().x, selected.getEndPoint().y);
            }

            if (currentLine != null) {
                g.drawLine(currentLine.getStartPoint().x, currentLine.getStartPoint().y, currentLine.getEndPoint().x, currentLine.getEndPoint().y);
            }
        }
    }

    // Класс, представляющий линию с начальной и конечной точками
    class Line {
        private Point startPoint, endPoint;

        public Line(Point startPoint, Point endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Point getStartPoint() {
            return startPoint;
        }

        public Point getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(Point endPoint) {
            this.endPoint = endPoint;
        }

        public double getLength() {
            return Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));
        }
    }

    // Главный метод для запуска редактора
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphicEditor());
    }
}
