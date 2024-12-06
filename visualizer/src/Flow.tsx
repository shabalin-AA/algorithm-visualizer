import React, { useCallback, useState, useRef } from "react";
import {
    ReactFlow,
    useReactFlow,
    ReactFlowProvider,
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
} from "@xyflow/react";
import axios from "axios";

import "@xyflow/react/dist/style.css";
import NodeIf from "./NodeIf";
import NodeCalc from "./NodeCalc";
import DeletableEdge from "./DeletableEdge";
import RightSidebar from "./RightSidebar";
import { DnDProvider, useDnD } from "./DnDContext";
import ContextMenu from "./ContextMenu";
import LeftSidebar from "./LeftSidebar";
import SaveFlowchart from "./SaveProject";

interface Menu {
    id: string;
    top: number | boolean;
    left: number | boolean;
    right: number | boolean;
    bottom: number | boolean;
}

let id = 1;
const getId = () => `${id++}`;

const initialNodes: Node[] = [];
const initialEdges: Edge[] = [];

const nodeTypes = {
    NodeIf: NodeIf,
    NodeCalc: NodeCalc,
};

const edgeTypes = {
    DeletableEdge: DeletableEdge,
};

const url = "http://localhost:3000/";

const BasicFlow = () => {
    const reactFlowWrapper = useRef(null);
    const { screenToFlowPosition } = useReactFlow();
    const [type] = useDnD();
    const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
    const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
    const [menu, setMenu] = useState<Menu | null>(null);
    const ref = useRef<HTMLDivElement | null>(null);

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
                node.data.result = resultJson.err.toString();
            }
            return node;
        }
        axios
            .post(url + "execute", jo)
            .then((response) => {
                const results = response.data;
                setNodes((nodes) =>
                    nodes.map((node) => newResult(node, results[+node.id])),
                );
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

    function GetFlowchart() {
        const id = 1;
        axios
            .get(url + "flowchart/" + id)
            .then((response) => {
                console.log(response);
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
        (event: {
            preventDefault: () => void;
            dataTransfer: { dropEffect: string };
        }) => {
            event.preventDefault();
            event.dataTransfer.dropEffect = "move";
        },
        [],
    );

    const onDrop = useCallback(
        (event: { preventDefault: () => void; clientX: any; clientY: any }) => {
            event.preventDefault();
            if (!type) return;
            const position = screenToFlowPosition({
                x: event.clientX,
                y: event.clientY,
            });
            const newNode = {
                id: getId(),
                type,
                position,
                data: { code: "", result: "null" },
            };
            setNodes((nds) => nds.concat(newNode));
        },
        [screenToFlowPosition, type, setNodes],
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
            const pane = ref.current!.getBoundingClientRect();
            setMenu({
                id: node.id,
                top: event.clientY < pane.height - 200 ? event.clientY : false,
                left: event.clientX < pane.width - 200 ? event.clientX : false,
                right:
                    event.clientX >= pane.width - 200
                        ? pane.width - event.clientX
                        : false,
                bottom:
                    event.clientY >= pane.height - 200
                        ? pane.height - event.clientY
                        : false,
            });
        },
        [setMenu],
    );

    const onPaneClick = useCallback(() => setMenu(null), [setMenu]);

    const [isLeftSidebarOpen, setIsLeftSidebarOpen] = useState(false);
    const toggleLeftSidebar = () => {
        setIsLeftSidebarOpen(!isLeftSidebarOpen);
    };

    const fromJson = function (obj: any) {
        const fullObj = JSON.parse(obj["fullJson"]);
        console.log(fullObj, typeof fullObj);
        return fullObj;
    };

    const handleSelectItem = (json: string) => {
        const flowchart = JSON.parse(json);
        setNodes((_) => flowchart.Nodes.map(fromJson));
        setEdges((_) => flowchart.Edges.map(fromJson));
    };

    return (
        <div className="BasicFlow">
            <button className="hamburger-btn" onClick={toggleLeftSidebar}>
                <div
                    className={`line ${isLeftSidebarOpen ? "open" : ""}`}
                ></div>
                <div
                    className={`line ${isLeftSidebarOpen ? "open" : ""}`}
                ></div>
                <div
                    className={`line ${isLeftSidebarOpen ? "open" : ""}`}
                ></div>
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
                    // panOnScroll = {true}
                    // selectionOnDrag = {true}
                    // panOnDrag={panOnDrag}
                    // selectionMode={SelectionMode.Partial}
                    nodeTypes={nodeTypes}
                    edgeTypes={edgeTypes}
                    onDrop={onDrop}
                    onDragOver={onDragOver}
                    onPaneClick={onPaneClick}
                    onNodeContextMenu={onNodeContextMenu}
                    fitView
                >
                    <MiniMap pannable zoomable />
                    <Panel className="inline-container" position="top-center">
                        <div className="inline-item">
                            <button
                                className="play-button"
                                onClick={PostExecute}
                            >
                                <span className="play-icon">▶</span>
                            </button>
                        </div>
                        <div className="inline-item">
                            <SaveFlowchart onSave={PostSave} />
                        </div>
                    </Panel>
                    <Background />
                    {menu && <ContextMenu onClick={onPaneClick} {...menu} />}
                </ReactFlow>
            </div>
            <RightSidebar />
        </div>
    );
};

let flow = () => (
    <ReactFlowProvider>
        <DnDProvider>
            <BasicFlow />
        </DnDProvider>
    </ReactFlowProvider>
);

export default flow;
