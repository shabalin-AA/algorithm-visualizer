import { Node, Position, NodeProps, Handle, useReactFlow} from "@xyflow/react"
import { memo } from 'react';
import "./styles.css"
import LimitedConnectionHandle from "./LimitedConnectionHandle"

type Text = Node<{code: string}>;

export default function NodeIf({id, data}: NodeProps<Text>) {
  const { updateNodeData } = useReactFlow();
  return (
    <div>
      <div style={{
        width: "50px",
        height: "50px",
        background: '#fff',
        border: '1px solid #777',
        transform: "rotate(45deg)",
        }}>
      </div>
      <input
         className="node-if"
         placeholder="a > b"
         value = {data.code}
         onChange={(evt) => updateNodeData(id, { text: evt.target.value })}
      />
      <div>
        <LimitedConnectionHandle type="source" position={Position.Left}  style={{left: -9}}  connectioncount={1} id="false"/>
        <LimitedConnectionHandle type="source" position={Position.Right} style={{right: -9}} connectioncount={1} id="true"/>
        <Handle type="target" position={Position.Top}   style={{top: -8}}/>
      </div>
    </div>
  );
};

