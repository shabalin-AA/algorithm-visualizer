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
import "./Static.css";
import NodeIf from "./NodeIf";
import NodeCalc from "./NodeCalc";
import NodeSubflow from "./NodeSubflow";
import DeletableEdge from "./DeletableEdge";
import NodeContextMenu from "./NodeContextMenu";
import { NodeContextMenuProps } from "./NodeContextMenu";
import { FlowchartListItem, LeftSidebarProps } from "./LeftSidebar";
import NewNodeMenu, { NewNodeMenuProps } from "./NewNodeMenu";
import LeftSidebar from "./LeftSidebar";
import PlaySavePanel from "./PlaySavePanel";

const BasicFlow = () => {
    const reactFlowWrapper = useRef(null);
    const ref = useRef<HTMLDivElement | null>(null);

    const [nodes, setNodes, onNodesChange] = useNodesState<Node>([]);
    const [edges, setEdges, onEdgesChange] = useEdgesState<Edge>([]);

    const [executing, setExecuting] = useState(false);

    const [nodeContextMenu, setNodeContextMenu] = useState<NodeContextMenuProps>({
        id: "",
        position: { x: 0, y: 0 },
        visible: false,
    });

    const selectFlowchartListItem = (name: string, json: string) => {
        const flowchart = JSON.parse(json);
        setNodes((_) => flowchart.Nodes.map(fromJson));
        setEdges((_) => flowchart.Edges.map(fromJson));
        setFlowchartName(name);
    };
    async function flowchart_list() {
        await axios
            .get(process.env.REACT_APP_API_URL + "/flowchart-list")
            .then((response) => {
                const data: FlowchartListItem[] = response.data;
                setLeftSidebar({ ...leftSidebar, flowchartList: data });
            })
            .catch((error) => console.log(error));
    }
    const [leftSidebar, setLeftSidebar] = useState<LeftSidebarProps>({
        visible: false,
        flowchartList: [],
        updateFlowchartList: flowchart_list,
        onSelectItem: selectFlowchartListItem,
    });
    const [flowchartName, setFlowchartName] = useState("");

    const [newNodeMenu, setNewNodeMenu] = useState<NewNodeMenuProps>({
        visible: false,
        position: { x: 0, y: 0 },
    });

    const onReconnect = useCallback(
        (oldEdge: Edge, newConnection: Connection) => setEdges((eds) => reconnectEdge(oldEdge, newConnection, eds)),
        [setEdges],
    );

    const onConnect = useCallback(
        (connection: any) => {
            const edge = { ...connection, type: "DeletableEdge" };
            setEdges((eds) => addEdge(edge, eds));
        },
        [setEdges],
    );

    const onDragOver = useCallback((event: { preventDefault: () => void; dataTransfer: { dropEffect: string } }) => {
        event.preventDefault();
        event.dataTransfer.dropEffect = "move";
    }, []);

    function nodeJson(node: Node) {
        let type = "";
        switch (node.type) {
            case "NodeIf":
                type = "COND";
                break;
            case "NodeCalc":
                type = "CALC";
                break;
            case "NodeSubflow":
                type = "SUBFLOW";
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
            if (resultJson.ok !== undefined) {
                node.data.result = resultJson.ok.toString();
            } else if (resultJson.err) {
                node.data.result = resultJson.err.toString();
            } else {
                node.data.result = "";
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
            .then()
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

    const onPaneClick = useCallback(() => {
        setNewNodeMenu((menu) => {
            return { ...menu, visible: false };
        });
        setNodeContextMenu((menu) => {
            return { ...menu, visible: false };
        });
        setLeftSidebar((sidebar) => {
            return { ...sidebar, visible: false };
        });
    }, [setNodeContextMenu, setNewNodeMenu, setLeftSidebar]);

    const fromJson = function (obj: any) {
        const fullObj = JSON.parse(obj["fullJson"]);
        return fullObj;
    };

    return (
        <div className="BasicFlow" style={{ top: "-10px", height: "95vh", position: "relative" }}>
            <LeftSidebar {...leftSidebar} />
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
                        NodeSubflow: NodeSubflow,
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
                        flowchartName={() => flowchartName}
                        setFlowchartName={setFlowchartName}
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
