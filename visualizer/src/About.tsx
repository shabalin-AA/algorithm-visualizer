import React from "react";
import "./Static.css"; // Импортируем CSS файл
import MyFooter from "./MyFooter";

const About: React.FC = () => {
    return (
        <div className="page">
            <div className="container">
                <h1>О проекте</h1>
                <p>
                    Добро пожаловать в визуальный редактор алгоритмов! Это инструмент, который
                    позволяет визуализировать логику бизнес-процессов с помощью блок-схем.
                </p>

                <h2>Визуальное программирование</h2>
                <p>
                    Визуальное программирование — это подход к программированию, который позволяет
                    пользователям создавать программы с помощью графических элементов, таких как
                    блоки, вместо написания кода. Этот метод упрощает процесс разработки, делая его
                    более интуитивным и доступным для людей без глубоких знаний в программировании.
                    Визуальное программирование позволяет сосредоточиться на логике и структуре
                    программы, что делает его идеальным инструментом для обучения и быстрого
                    прототипирования.
                </p>

                <h2>Основные функции</h2>
                <ul>
                    <li>Создание блок-схем с помощью простого перетаскивания элементов</li>
                    <li>Настройка свойств блоков и соединений для точного отображения логики</li>
                    <li>Выполнение созданных блок-схем для проверки их работоспособности</li>
                    <li>Сохранение и загрузка ваших проектов для дальнейшей работы</li>
                </ul>

                <h2>Как это работает</h2>
                <p>
                    Просто начните с выбора элементов блок-схемы из контекстного меню,
                    перетаскивайте их на рабочей области и соединяйте с помощью линий. После
                    завершения вы можете запустить выполнение схемы, чтобы увидеть, как она
                    работает.
                </p>

                <h2>Контакты</h2>
                <p>
                    Если у вас есть вопросы или предложения, не стесняйтесь обращаться к нам по
                    электронной почте: support@example.com.
                </p>
            </div>
            <MyFooter />
        </div>
    );
};

export default About;
