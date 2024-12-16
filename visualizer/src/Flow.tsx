import React, { useCallback, useState, useRef } from "react";
import axios from "axios";
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
    reconnectEdge,
} from "@xyflow/react";

import "@xyflow/react/dist/style.css";
import NodeIf from "./NodeIf";
import NodeCalc from "./NodeCalc";
import DeletableEdge from "./DeletableEdge";
import NodeContextMenu from "./NodeContextMenu";
import { NodeContextMenuProps } from "./NodeContextMenu";
import NewNodeMenu, { NewNodeMenuProps } from "./NewNodeMenu";
import LeftSidebar from "./LeftSidebar";
import PlaySavePanel from "./PlaySavePanel";

const BasicFlow = () => {
    const reactFlowWrapper = useRef(null);
    const [executing, setExecuting] = useState(false);
    const ref = useRef<HTMLDivElement | null>(null);
    const [nodes, setNodes, onNodesChange] = useNodesState<Node>([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState<Edge>([]);
    const [nodeContextMenu, setNodeContextMenu] = useState<NodeContextMenuProps>({
        id: "",
        position: { x: 0, y: 0 },
        visible: false,
    });
    const [newNodeMenu, setNewNodeMenu] = useState<NewNodeMenuProps>({
        visible: false,
        position: { x: 0, y: 0 },
    });

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

    function halt() {
        setExecuting(false);
        axios.get(process.env.REACT_APP_API_URL + "/halt").catch((error) => console.log(error));
        return;
    }

    async function execute() {
        setExecuting(true);
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
        await axios
            .post(process.env.REACT_APP_API_URL + "/execute", jo)
            .then((response) => {
                const results = response.data;
                setNodes(nodes.map((node) => newResult(node, results[+node.id])));
            })
            .catch((error) => {
                console.log(error);
            });
        setExecuting(false);
    }

    async function save(name: string) {
        let jo = {
            Nodes: nodes.map(nodeJson),
            Edges: edges.map(edgeJson),
        };
        await axios
            .post(process.env.REACT_APP_API_URL + "/save/" + name, jo)
            .then((response) => {
                //TODO успешное/не успешное сохранение
            })
            .catch((error) => console.log(error));
    }

    const onNodeContextMenu = useCallback(
        (event: React.MouseEvent, node: Node) => {
            event.preventDefault();
            setNodeContextMenu({
                ...nodeContextMenu,
                id: node.id,
                position: { x: event.clientX, y: event.clientY },
                visible: true,
            });
        },
        [setNodeContextMenu, nodeContextMenu],
    );

    const onContextMenu = (event: React.MouseEvent<HTMLDivElement>) => {
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
        setNodeContextMenu((menu) => {
            return { ...menu, visible: false };
        });
        setIsLeftSidebarOpen(false);
    }, [setNodeContextMenu, setIsLeftSidebarOpen, setNewNodeMenu]);

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
        <div className="BasicFlow" style={{ top: "-10px", height: "95vh", position: "relative" }}>
            <button className="hamburger-btn" onClick={toggleLeftSidebar}>
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
                    onContextMenu={onContextMenu}
                    fitView
                >
                    <NodeContextMenu {...nodeContextMenu} />
                    <MiniMap pannable zoomable />
                    <PlaySavePanel
                        halt={halt}
                        execute={execute}
                        save={save}
                        isExecuting={() => executing}
                    />
                    <Background />
                    {!nodeContextMenu.visible && <NewNodeMenu {...newNodeMenu} />}
                </ReactFlow>
            </div>
        </div>
    );
};

let flow = () => <BasicFlow />;

export default flow;
