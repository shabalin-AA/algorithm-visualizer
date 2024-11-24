import React, { useCallback, useState, useRef} from "react";
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
  reconnectEdge
} from "@xyflow/react";
import axios from "axios"

import "@xyflow/react/dist/style.css";
import NodeIf from "./NodeIf";
import NodeCalc from "./NodeCalc";
import DeletableEdge from './DeletableEdge';
import Sidebar from './Sidebar';
import { DnDProvider, useDnD } from './DnDContext';
import ContextMenu from './ContextMenu';


const url = "http://localhost:3000/"

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
    .then((response) => {
      const results = response.data;
      for (let i = 0; i < nodes.length; i++) {
        let node = nodes[i];
        let id: number = +node.id;
        node.data.result = results[id];
      }
      setNodes((nds) => (nodes));
    })
    .catch((error) => console.log(error))
  }
  

  const onReconnect = useCallback(
    (oldEdge: Edge, newConnection: Connection) =>
      setEdges((eds) => reconnectEdge(oldEdge, newConnection, eds)),
    [],
  );
  
  const onConnect = useCallback(
    (connection: any) => {
      const edge = { ...connection, type: 'DeletableEdge' };
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
        data: { code: '', result: 'null' },
      };
      setNodes((nds) => nds.concat(newNode));
    },
    [screenToFlowPosition, type],
  );

  const onNodeContextMenu = useCallback(
    (event: { preventDefault: () => void; clientY: number; clientX: number; }, node: { id: any; }) => {
      event.preventDefault();
      const pane = ref.current!.getBoundingClientRect();
      setMenu({
        id: node.id,
        top: event.clientY < pane.height - 200 ? event.clientY : false,
        left: event.clientX < pane.width - 200 ? event.clientX : false,
        right: event.clientX >= pane.width - 200 ? pane.width - event.clientX : false,
        bottom: event.clientY >= pane.height - 200 ? pane.height - event.clientY : false,
      });
    },
    [setMenu],
  );

  const onPaneClick = useCallback(() => setMenu(null), [setMenu]);

  return (
    <div className="BasicFlow">
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
