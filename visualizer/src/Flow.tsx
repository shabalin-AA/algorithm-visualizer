import React, { useCallback, useState, useRef} from "react";
import {
  ReactFlow,
  useReactFlow,
  ReactFlowProvider,
  Node,
  addEdge,
  SelectionMode,
  Background,
  BackgroundVariant,
  MiniMap,
  Edge,
  Connection,
  useNodesState,
  useEdgesState,
  Panel,
  Controls, 
  ControlButton,
  reconnectEdge
} from "@xyflow/react";

import "@xyflow/react/dist/style.css";
import CustomNodeIf from "./CustomNodeIf";
import CustomNodeInput from "./CustomNodeInput";
import CustomNodeDefault from "./CustomNodeDefault";
import CustomNodeOutput from "./CustomNodeOutput";
import CustomEdge from './CustomEdge';
import axios from "axios"
import Sidebar from './Sidebar';
import { DnDProvider, useDnD } from './Context';
import ContextMenu from './ContextMenu';

const panOnDrag = [1, 2];
const url = "http://localhost:3000/execute"

interface Menu {
  id: string;
  top: number;
  left: number;
  right?: number;
  bottom?: number;
}

let id = 1;
const getId = () => `${id++}`;

const initialNodes: Node[] = [
];

const initialEdges: Edge[] = [
];

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

  function AxiosPost(){
    axios.post(url, {
      Nodes: nodes,
      Edges: edges
    })
    .then(function (response) {
      console.log(response);
    })
    .catch(function (error) {
      console.log(error);
    });
  }
  

  const onReconnect = useCallback(
    (oldEdge: Edge, newConnection: Connection) =>
      setEdges((els) => reconnectEdge(oldEdge, newConnection, els)),
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
      if (!type) {
        return;
      }
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
        fitView
        >
        <MiniMap
        pannable zoomable
        />

        <Background
          id="1"
          gap={25}
          color="#f1f1f1"
          variant={BackgroundVariant.Lines}
        />
  
        <Background
          id="2"
          gap={100}
          color="#98ff98"
          variant={BackgroundVariant.Lines}
        />
        <Panel>
          <h3>Оправить запрос</h3>
            <button onClick={() => AxiosPost()}>отправить</button>
        </Panel>
        <Background />
        {menu && <ContextMenu onClick={onPaneClick} {...menu} />}
      </ReactFlow>
      </div>
      <Sidebar />
    </div>
  );
}

export default () => (
  <ReactFlowProvider>
    <DnDProvider>
      <BasicFlow />
    </DnDProvider>
  </ReactFlowProvider>
);