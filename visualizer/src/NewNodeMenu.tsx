import "./NewNodeMenu.css";
import { useReactFlow } from "@xyflow/react";
import React, { useCallback } from "react";

export interface NewNodeMenuProps {
    visible: boolean;
    position: { x: number; y: number };
}

const NewNodeMenu: React.FC<NewNodeMenuProps> = ({ visible, position }) => {
    const { setNodes, getNodes } = useReactFlow();

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
        (type: string, x: number, y: number) => {
            const newNode = {
                id: getId(),
                type,
                position: { x: x, y: y },
                data: { code: "", result: "" },
                measured: { width: 150, height: 150 },
            };
            setNodes((nds) => nds.concat(newNode));
        },
        [setNodes, getId],
    );

    return (
        <>
            {visible && (
                <ul
                    className="newNodeMenu-list"
                    style={{
                        position: "absolute",
                        top: position.y,
                        left: position.x,
                    }}
                >
                    <div>Добавить</div>
                    <li
                        className="newNodeMenu-element"
                        onClick={(e) => newNode("NodeCalc", e.clientX, e.clientY)}
                    >
                        Узел расчета
                    </li>
                    <li
                        className="newNodeMenu-element"
                        onClick={(e) => newNode("NodeIf", e.clientX, e.clientY)}
                    >
                        Узел ветвления
                    </li>
                </ul>
            )}
        </>
    );
};

export default NewNodeMenu;
