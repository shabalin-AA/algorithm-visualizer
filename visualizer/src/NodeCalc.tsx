import { Node, Position, NodeProps, Handle, useReactFlow} from "@xyflow/react"
import { memo } from 'react';
import "./styles.css"
import LimitedConnectionHandle from "./LimitedConnectionHandle"

type Text = Node<{code: string}>;

export default function NodeCalc({id, data}: NodeProps<Text>) {
  const { updateNodeData } = useReactFlow();
  return (
    <div>
      <div style={{
        width: "200px",
        height: "30px",
        background: '#fff',
        border: '1px solid #777',
        }}>
        <textarea
         value={data.code}
         className="node-code"
         placeholder="c = b + a"
         onChange={(evt) => updateNodeData(id, {text: evt.target.value})}
        />
      </div>
      <div>
          <LimitedConnectionHandle type="target" position={Position.Top} connectioncount={-1}/>
          <LimitedConnectionHandle type="source" position={Position.Bottom} connectioncount={1}/>
      </div>
    </div>
  );
};

