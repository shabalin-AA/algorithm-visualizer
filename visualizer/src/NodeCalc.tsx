import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer} from "@xyflow/react"
import { memo } from 'react';
import "./styles.css"
import LimitedConnectionHandle from "./LimitedConnectionHandle"

type Text = Node<{code: string, result: string}>;


export default function NodeCalc({id, data}: NodeProps<Text>, selected: boolean | undefined) {
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
         onChange={(evt) => updateNodeData(id, {code: evt.target.value})}
        />
      </div>
      <div>
        <Handle type="target" position={Position.Top}/>
        <LimitedConnectionHandle type="source" position={Position.Bottom} connectioncount={1}/>
      </div>
      <div>
        {data.result}
      </div>
    </div>
  );
};

