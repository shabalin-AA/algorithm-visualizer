import { useCallback } from "react";
import { useState } from "react";
import {
  ReactFlow,
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
  ControlButton
} from "@xyflow/react";
import CalcBlock from "./CustomNode";
import "@xyflow/react/dist/style.css";
import NodeCustomPoints from "./CustomNodeBlock";
import ButtonEdge from './ButtonEdge';

// const panOnDrag = [1, 2];

const initialNodes: Node[] = [
  {
    id: "1",
    type: "input",
    data: { label: "Node 1" },
    position: { x: 250, y: 5 }
  },
  { id: "2", data: { label: "Node 2" }, position: { x: 100, y: 100 } },
  { id: "3", data: { label: "Node 3" }, position: { x: 400, y: 100 } },
  {
    id: "4",
    type: "CalcBlock",
    data: { label: "Calc Block" },
    position: { x: 400, y: 200 }
  },

  {
    id: "5",
    type: "NodeCustomPoints",
    data: { label: "While" },
    position: { x: 185, y: 250 },
    style: {
      background: '#fff',
      border: '1px solid #777',
      fontSize: 15,
      padding: 10,
    },
  },
  { id: "6", data: { label: "Node 4" }, position: { x: 150, y: 350 } },
  

];

const initialEdges: Edge[] = [
  { id: "e1-2", source: "1", target: "2", animated: true },
  { id: "e1-3", source: "1", target: "3" },
  { id: "e5-6", source: "5", target: "6", type: 'buttonedge'},
  { id: "e6-5", source: "6", target: "5", type: 'step',}
];

const nodeTypes = {
  CalcBlock: CalcBlock,
  NodeCustomPoints: NodeCustomPoints
};

const edgeTypes = {
  buttonedge: ButtonEdge,
};


const BasicFlow = () => {
  const [nodes, , onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
  const onConnect = useCallback(
    (params: Edge | Connection) => setEdges((eds) => addEdge(params, eds)),
    [setEdges]
  );

  return (
    <ReactFlow
      nodes={nodes}
      edges={edges}
      onNodesChange={onNodesChange}
      onEdgesChange={onEdgesChange}
      onConnect={onConnect}
      // panOnScroll = {true}
      // selectionOnDrag = {true}
      // panOnDrag={panOnDrag}
      // selectionMode={SelectionMode.Partial}
      nodeTypes={nodeTypes}
      edgeTypes={edgeTypes}
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

    </ReactFlow>
  );
}


export default BasicFlow;

