import React, { useCallback, useState, useRef } from "react";
import {
    ReactFlow,
    type Node,
    type Edge,
    addEdge,
    Background,
    MiniMap,
    Connection,
    useNodesState,
    useEdgesState,
    Panel,
    reconnectEdge,
    useReactFlow,
} from "@xyflow/react";
import axios from "axios";

import "@xyflow/react/dist/style.css";
import NodeIf from "./NodeIf";
import NodeCalc from "./NodeCalc";
import DeletableEdge from "./DeletableEdge";
import NodeContextMenu from "./NodeContextMenu";
import { NodeContextMenuProps } from "./NodeContextMenu";
import NewNodeMenu, { NewNodeMenuProps } from "./NewNodeMenu";
import LeftSidebar from "./LeftSidebar";
import SaveFlowchart from "./SaveProject";

let id = 1;
const getId = () => `${id++}`;

const url = "http://localhost:3000/";

const BasicFlow = () => {
    const reactFlowWrapper = useRef(null);
    const [nodes, setNodes, onNodesChange] = useNodesState<Node>([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState<Edge>([]);
    const [nodeContextMenu, setNodeContextMenu] = useState<NodeContextMenuProps | null>(null);
    const ref = useRef<HTMLDivElement | null>(null);
    const [flowResult, setFlowResult] = useState<object>();

    const newNode = useCallback(
        (type: string, x: number, y: number) => {
            const newNode = {
                id: getId(),
                type,
                position: { x: x, y: y },
                data: { code: "", result: "null" },
                measured: { width: 150, height: 150 },
            };
            setNodes((nds) => nds.concat(newNode));
            setNewNodeMenu((menu) => {
                return { ...menu, visible: false };
            });
        },
        [setNodes],
    );

    const [newNodeMenu, setNewNodeMenu] = useState<NewNodeMenuProps>({
        visible: false,
        position: { x: 0, y: 0 },
        newNode: newNode,
    });

    function nodeJson(node: Node) {
        let type = "";
        switch (node.type) {
            case "NodeIf":
                type = "COND";
                break;
            case "NodeCalc":
                type = "CALC";
                break;
        }
        return {
            id: node.id,
            type: type,
            code: node.data.code,
            fullJson: JSON.stringify(node),
        };
    }

    let edgeId = 0;
    function edgeJson(edge: Edge) {
        edgeId++;
        let branch = "true";
        if (edge.id.includes("false")) branch = "false";
        return {
            id: String(edgeId),
            source: edge.source,
            target: edge.target,
            branch: branch,
            fullJson: JSON.stringify(edge),
        };
    }

    function PostExecute() {
        let jo = {
            Nodes: nodes.map(nodeJson),
            Edges: edges.map(edgeJson),
        };
        function newResult(node: Node, resultJson: any) {
            try {
                node.data.result = resultJson.result.toString();
            } catch {
                //node.data.result = resultJson.err.toString();
            }
            return node;
        }
        axios
            .post(url + "execute", jo)
            .then((response) => {
                const results = response.data;
                setNodes((nodes) => nodes.map((node) => newResult(node, results[+node.id])));
            })
            .catch((error) => console.log(error));
    }

    function PostSave(name: string) {
        let jo = {
            Nodes: nodes.map(nodeJson),
            Edges: edges.map(edgeJson),
        };
        axios
            .post(url + "save/" + name, jo)
            .then((response) => {
                //TODO успешное/не успешное сохранение
            })
            .catch((error) => console.log(error));
    }

    const onReconnect = useCallback(
        (oldEdge: Edge, newConnection: Connection) =>
            setEdges((eds) => reconnectEdge(oldEdge, newConnection, eds)),
        [setEdges],
    );

    const onConnect = useCallback(
        (connection: any) => {
            const edge = { ...connection, type: "DeletableEdge" };
            setEdges((eds) => addEdge(edge, eds));
        },
        [setEdges],
    );

    const onDragOver = useCallback(
        (event: { preventDefault: () => void; dataTransfer: { dropEffect: string } }) => {
            event.preventDefault();
            event.dataTransfer.dropEffect = "move";
        },
        [],
    );

    const onNodeContextMenu = useCallback(
        (
            event: {
                preventDefault: () => void;
                clientY: number;
                clientX: number;
            },
            node: { id: any },
        ) => {
            event.preventDefault();
            setNodeContextMenu({
                id: node.id,
                left: event.clientX,
                right: event.clientX + 200,
                top: event.clientY,
                bottom: event.clientY + 300,
            });
        },
        [setNodeContextMenu],
    );

    const handleContextMenu = (event: React.MouseEvent<HTMLDivElement>) => {
        event.preventDefault();
        event.stopPropagation();
        setNewNodeMenu((menu) => {
            return {
                ...menu,
                visible: true,
                position: { x: event.pageX, y: event.pageY },
            };
        });
    };

    const [isLeftSidebarOpen, setIsLeftSidebarOpen] = useState(false);
    const toggleLeftSidebar = () => {
        setIsLeftSidebarOpen(!isLeftSidebarOpen);
    };

    const onPaneClick = useCallback(() => {
        setNewNodeMenu((menu) => {
            return { ...menu, visible: false };
        });
        setNodeContextMenu(null);
        setIsLeftSidebarOpen(false);
    }, [setNodeContextMenu, setIsLeftSidebarOpen]);

    const fromJson = function (obj: any) {
        const fullObj = JSON.parse(obj["fullJson"]);
        return fullObj;
    };

    const handleSelectItem = (json: string) => {
        const flowchart = JSON.parse(json);
        setNodes((_) => flowchart.Nodes.map(fromJson));
        setEdges((_) => flowchart.Edges.map(fromJson));
    };

    return (
        <div className="BasicFlow" style={{ height: "100vh", position: "relative" }}>
            <button className="hamburger-btn" onClick={toggleLeftSidebar}>
                <div className={`line ${isLeftSidebarOpen ? "open" : ""}`}></div>
                <div className={`line ${isLeftSidebarOpen ? "open" : ""}`}></div>
                <div className={`line ${isLeftSidebarOpen ? "open" : ""}`}></div>
            </button>
            <LeftSidebar
                isOpen={isLeftSidebarOpen}
                onClose={toggleLeftSidebar}
                onSelectItem={handleSelectItem}
            />
            <div className="reactflow-wrapper" ref={reactFlowWrapper}>
                <ReactFlow
                    ref={ref}
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    onReconnect={onReconnect}
                    nodeTypes={{
                        NodeIf: NodeIf,
                        NodeCalc: NodeCalc,
                    }}
                    edgeTypes={{
                        DeletableEdge: DeletableEdge,
                    }}
                    onDragOver={onDragOver}
                    onPaneClick={onPaneClick}
                    onNodeContextMenu={onNodeContextMenu}
                    onContextMenu={handleContextMenu}
                    fitView
                >
                    <MiniMap pannable zoomable />
                    <Panel className="inline-container" position="top-right">
                        <div className="inline-item">
                            <button className="play-button" onClick={PostExecute}>
                                <span className="play-icon">▶</span>
                            </button>
                        </div>
                        <div className="inline-item">
                            <SaveFlowchart onSave={PostSave} />
                        </div>
                        {nodeContextMenu && <NodeContextMenu {...nodeContextMenu} />}
                    </Panel>
                    <Background />
                </ReactFlow>
            </div>
            {newNodeMenu.visible && !nodeContextMenu && <NewNodeMenu {...newNodeMenu} />}
        </div>
    );
};

let flow = () => <BasicFlow />;

export default flow;
