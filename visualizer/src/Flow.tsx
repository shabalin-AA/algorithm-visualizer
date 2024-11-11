import React, { useCallback, useState, useRef} from "react";
import {
  ReactFlow,
  useReactFlow,
  ReactFlowProvider,
  Node,
  addEdge,
  Background,
  MiniMap,
  Edge,
  Connection,
  useNodesState,
  useEdgesState,
  Panel,
  reconnectEdge
} from "@xyflow/react";
import axios from "axios"

import "@xyflow/react/dist/style.css";
import CustomNodeIf from "./CustomNodeIf";
import CustomNodeInput from "./CustomNodeInput";
import CustomNodeDefault from "./CustomNodeDefault";
import CustomNodeOutput from "./CustomNodeOutput";
import CustomEdge from './CustomEdge';
import Sidebar from './Sidebar';
import { DnDProvider, useDnD } from './Context';
import ContextMenu from './ContextMenu';

const url = "http://localhost:3000/"

interface Menu {
  id: string;
  top: number;
  left: number;
  right?: number;
  bottom?: number;
}

let id = 1;
const getId = () => `${id++}`;

const initialNodes: Node[] = [];
const initialEdges: Edge[] = [];

const nodeTypes = {
  CustomNodeIf: CustomNodeIf,
  CustomNodeInput: CustomNodeInput,
  CustomNodeDefault: CustomNodeDefault,
  CustomNodeOutput: CustomNodeOutput
};

const edgeTypes = {
  CustomEdge: CustomEdge,
};

const BasicFlow = () => {
  const reactFlowWrapper = useRef(null);
  const { screenToFlowPosition } = useReactFlow();
  const [type] = useDnD();
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
  const [menu, setMenu] = useState<Menu | null>(null);

  function nodeJson(node: Node) {
    let type = "";
    switch (node.type) {
      case "CustomNodeIf": 
        type = "COND";
        break;
      case "CustomNodeDefault":
        type = "CALC";
        break;
    }
    return {
      id: node.id,
      type: type,
      code: node.data.label,
    };
  }

  let edgeId = 0;
  function edgeJson(edge: Edge) {
    edgeId++;
    let branch = 'true';
    if (edge.id.includes('false'))
      branch = 'false';
    return {
      id: String(edgeId),
      source: edge.source,
      target: edge.target,
      branch: branch
    }
  }

  function PostExecute(){
    let jo = {
      Nodes: nodes.map(nodeJson),
      Edges: edges.map(edgeJson)
    };
    axios.post(url + "execute", jo)
    .then((response) => console.log(response.data))
    .catch((error) => console.log(error))
  }
  

  const onReconnect = useCallback(
    (oldEdge: Edge, newConnection: Connection) =>
      setEdges((eds) => reconnectEdge(oldEdge, newConnection, eds)),
    [],
  );
  
  const onConnect = useCallback(
    (connection: any) => {
      const edge = { ...connection, type: 'CustomEdge' };
      setEdges((eds) => addEdge(edge, eds));
    },
    [setEdges],
  );

  const onDragOver = useCallback((event: { preventDefault: () => void; dataTransfer: { dropEffect: string; }; }) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event: { preventDefault: () => void; clientX: any; clientY: any; }) => {
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
        data: { label: `Node ${id - 1}` },
      };
      setNodes((nds) => nds.concat(newNode));
    },
    [screenToFlowPosition, type],
  );

  const onNodeContextMenu = useCallback(
    (event: { preventDefault: () => void; clientY: number; clientX: number; }, node: Node) => {
      event.preventDefault();
      setMenu({
        id: node.id,
        top: 1,
        left: 1,
        right: 1,
        bottom: 1
      });
    },
    [setMenu],
  );

  const onPaneClick = useCallback(() => setMenu(null), [setMenu]);

  return (
    <div className="BasicFlow">
      <div className="reactflow-wrapper" ref={reactFlowWrapper}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        snapToGrid
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
        fitView>
        <MiniMap pannable zoomable/>
        <Panel>
          <button onClick={() => PostExecute()}>{">"}</button>
        </Panel>
        <Background />
        {menu && <ContextMenu onClick={onPaneClick} {...menu} />}
      </ReactFlow>
      </div>
      <Sidebar />
    </div>
  );
}

let flow = () => (
  <ReactFlowProvider>
    <DnDProvider>
      <BasicFlow />
    </DnDProvider>
  </ReactFlowProvider>
);

export default flow;
