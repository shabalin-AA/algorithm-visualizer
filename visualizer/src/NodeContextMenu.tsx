import React, { useCallback } from "react";
import { useReactFlow, Node, Edge } from "@xyflow/react";

export interface NodeContextMenuProps {
    id: string;
    left: number;
    right: number;
    top: number;
    bottom: number;
}

const NodeContextMenu: React.FC<NodeContextMenuProps> = ({
    id,
    left,
    right,
    top,
    bottom,
    ...props
}) => {
    const { getNode, setNodes, addNodes, setEdges } = useReactFlow();

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
                id: `${node.id + 1000}`,
                position,
            });
        }
    }, [id, getNode, addNodes]);

    const deleteNode = useCallback(() => {
        setNodes((nodes: Node[]) => nodes.filter((node: Node) => node.id !== id));
        setEdges((edges: Edge[]) => edges.filter((edge: Edge) => edge.source !== id));
    }, [id, setNodes, setEdges]);

    return (
        <div style={{ top, left, right, bottom }} {...props}>
            <p>
                <small>node: {id}</small>
            </p>
            <button onClick={duplicateNode}>duplicate</button>
            <button onClick={deleteNode}>delete</button>
        </div>
    );
};

export default NodeContextMenu;
