import React from "react";
import "./LeftSidebar.css"; // Импортируем CSS файл

interface SidebarProps {
    isOpen: boolean;
    onClose: () => void;
}

const LeftSidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
    return (
        <div className={`left-sidebar ${isOpen ? "open" : ""}`}>
            <h2>Меню</h2>
            <ul>
                <li>Пункт 1</li>
                <li>Пункт 2</li>
                <li>Пункт 3</li>
            </ul>
        </div>
    );
};

export default LeftSidebar;
