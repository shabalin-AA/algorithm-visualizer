import React, { useEffect, useState } from "react";
import axios from "axios";
import "./LeftSidebar.css"; // Импортируем CSS файл

interface SidebarProps {
    isOpen: boolean;
    onClose: () => void;
    onSelectItem: (json: string) => void;
}

interface Item {
    id: number; // или string, в зависимости от вашего API
    name: string;
    json: string;
}

const LeftSidebar: React.FC<SidebarProps> = ({
    isOpen,
    onClose,
    onSelectItem,
}) => {
    const [items, setItems] = useState<Item[]>([]);

    useEffect(() => {
        // Функция для получения данных из API
        const fetchData = async () => {
            axios
                .get("http://localhost:3000/flowchart-list")
                .then((response) => {
                    const data: Item[] = response.data;
                    setItems(data); // Устанавливаем полученные данные в состояние
                })
                .catch((error) => console.log(error));
        };

        fetchData(); // Вызов функции для получения данных
    }, []); // Пустой массив зависимостей, чтобы выполнить только один раз при монтировании

    const handleItemClick = (json: string) => {
        onSelectItem(json);
    };

    return (
        <div className={`left-sidebar ${isOpen ? "open" : ""}`}>
            <h2>Ваши схемы</h2>
            <ul>
                {items.map((item) => (
                    <li
                        key={item.id}
                        onClick={() => handleItemClick(item.json)}
                    >
                        {item.name}
                    </li> // Предполагаем, что у каждого элемента есть уникальный id и имя
                ))}
            </ul>
        </div>
    );
};

export default LeftSidebar;
