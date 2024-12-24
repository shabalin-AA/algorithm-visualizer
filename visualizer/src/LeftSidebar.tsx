import "./LeftSidebar.css";
import "./Static.css";
import React, { useState } from "react";

export interface FlowchartListItem {
    id: number;
    name: string;
    json: string;
}

export interface LeftSidebarProps {
    visible: boolean;
    flowchartList: FlowchartListItem[];
    updateFlowchartList: () => void;
    onSelectItem: (name: string, json: string) => void;
}

const LeftSidebar: React.FC<LeftSidebarProps> = ({
    flowchartList,
    updateFlowchartList,
    onSelectItem,
}) => {
    const [visible, setVisible] = useState(false);

    async function toggleLeftSidebar() {
        if (!visible) {
            updateFlowchartList();
        }
        setVisible(!visible);
    }

    const handleItemClick = (name: string, json: string) => {
        onSelectItem(name, json);
    };

    return (
        <>
            <button className="hamburger-btn" onClick={toggleLeftSidebar}>
                <div className="line"></div>
                <div className="line"></div>
                <div className="line"></div>
            </button>
            <div className={`left-sidebar ${visible ? "open" : ""}`}>
                <h2 style={{ marginLeft: "40px" }}>Ваши схемы</h2>
                <ul>
                    {flowchartList.map((item) => (
                        <li key={item.id} onClick={() => handleItemClick(item.name, item.json)}>
                            {item.name}
                        </li>
                    ))}
                </ul>
            </div>
        </>
    );
};

export default LeftSidebar;
