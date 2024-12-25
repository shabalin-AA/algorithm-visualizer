import "./NewNodeMenu.css";
import { useReactFlow } from "@xyflow/react";
import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";

export interface NewNodeMenuProps {
    visible: boolean;
    position: { x: number; y: number };
}
interface Item {
    id: number;
    name: string;
    json: string;
}

const NewNodeMenu: React.FC<NewNodeMenuProps> = ({ visible, position }) => {
    const { setNodes, getNodes } = useReactFlow();
    const [items, setItems] = useState<Item[]>([]);

    useEffect(() => {
        axios
            .get(process.env.REACT_APP_API_URL + "/flowchart-list")
            .then((response) => {
                const data: Item[] = response.data;
                setItems(data);
            })
            .catch((error) => console.log(error));
    }, []);

    const getId = useCallback(() => {
        const nodes = getNodes();
        if (nodes.length === 0) {
            return "1";
        }
        return (
            Math.max(
                ...nodes.map((node) => {
                    const matches = node.id.match(/\d+/);
                    if (matches === null) return 0;
                    return +matches[0];
                }),
            ) + 1
        ).toString();
    }, [getNodes]);

    const newNode = useCallback(
        (type: string, x: number, y: number, code?: string) => {
            const newNode = {
                id: getId(),
                type,
                position: { x: x, y: y },
                measured: { width: 150, height: 150 },
                data: { code: code, result: "" },
            };
            setNodes((nds) => nds.concat(newNode));
        },
        [setNodes, getId],
    );

    return (
        <>
            {visible && (
                <div
                    className="newNodeMenu"
                    style={{
                        position: "absolute",
                        top: position.y,
                        left: position.x,
                    }}
                >
                    <div>Добавить</div>
                    <ul>
                        <li onClick={(e) => newNode("NodeCalc", e.clientX, e.clientY)}>
                            Узел расчета
                        </li>
                        <li onClick={(e) => newNode("NodeIf", e.clientX, e.clientY)}>
                            Узел ветвления
                        </li>
                        {items.map((item) => (
                            <li
                                key={item.id}
                                onClick={(e) =>
                                    newNode("NodeSubflow", e.clientX, e.clientY, item.name)
                                }
                            >
                                {item.name}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </>
    );
};

export default NewNodeMenu;
