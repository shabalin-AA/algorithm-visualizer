import React, { useCallback } from "react";
import { useReactFlow, Node, Edge } from "@xyflow/react";
import "./NodeContextMenu.css";

export interface NodeContextMenuProps {
    id: string;
    visible: boolean;
    position: { x: number; y: number };
}

const NodeContextMenu: React.FC<NodeContextMenuProps> = ({ id, position, visible, ...props }) => {
    const { getNode, getNodes, setNodes, addNodes, setEdges } = useReactFlow();

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

    const duplicateNode = useCallback(() => {
        const node: Node | undefined = getNode(id);
        if (node) {
            const position = {
                x: node.position.x + 50,
                y: node.position.y + 50,
            };
            addNodes({
                ...node,
                selected: false,
                dragging: false,
                id: getId(),
                position,
            });
        }
    }, [id, getNode, addNodes, getId]);

    const deleteNode = useCallback(() => {
        setNodes((nodes: Node[]) => nodes.filter((node: Node) => node.id !== id));
        setEdges((edges: Edge[]) => edges.filter((edge: Edge) => edge.source !== id));
    }, [id, setNodes, setEdges]);

    return (
        <>
            {visible && (
                <div
                    style={{ top: position.y, left: position.x }}
                    className="nodeContextMenu"
                    {...props}
                >
                    <div>Узел {id}</div>
                    <ul>
                        <li className="nodeContextMenu-item" onClick={deleteNode}>
                            Удалить
                        </li>
                        <li className="nodeContextMenu-item" onClick={duplicateNode}>
                            Дублировать
                        </li>
                    </ul>
                </div>
            )}
        </>
    );
};

export default NodeContextMenu;
