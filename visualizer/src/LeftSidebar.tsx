import React, { useEffect, useState } from "react";
import axios from "axios";
import "./LeftSidebar.css";

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

const LeftSidebar: React.FC<SidebarProps> = ({ isOpen, onClose, onSelectItem }) => {
    const [items, setItems] = useState<Item[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            axios
                .get("http://localhost:8080/flowchart-list")
                .then((response) => {
                    const data: Item[] = response.data;
                    setItems(data);
                })
                .catch((error) => console.log(error));
        };
        fetchData();
    }, []);

    const handleItemClick = (json: string) => {
        onSelectItem(json);
    };

    return (
        <div className={`left-sidebar ${isOpen ? "open" : ""}`}>
            <h2>Ваши схемы</h2>
            <ul>
                {items.map((item) => (
                    <li key={item.id} onClick={() => handleItemClick(item.json)}>
                        {item.name}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default LeftSidebar;
