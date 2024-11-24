import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer} from "@xyflow/react"
import { memo } from 'react';
import "./styles.css"
import LimitedConnectionHandle from "./LimitedConnectionHandle"

type Text = Node<{code: string, result: string}>;

export default function NodeIf({id, data}: NodeProps<Text>, selected: boolean | undefined ) {
  const { updateNodeData } = useReactFlow();
  return (
    <div>
        <svg width="100" height="100" className="shape-svg">
          <g transform="translate(2, 2)">
            <path d="M0,48 L48,0 L96,48 L48,96 Z" fill="#ffffff" stroke-width="1" stroke="#000000" fill-opacity="0.8">
            </path>
          </g>
        </svg>
        <input
          className="node-code"
          placeholder="a > b"
          value = {data.code}
          onChange={(evt) => updateNodeData(id, { code: evt.target.value })}
        />
        <div>
          <Handle type="target" position={Position.Top} style={{top: 0, transform: "translate(-50%, -50%)"}}/>
          <LimitedConnectionHandle type="source" position={Position.Left}  style={{left: 0, top: 50, transform: "translate(-50%, -50%)"}}  connectioncount={1} id="false"/>
          <LimitedConnectionHandle type="source" position={Position.Right} style={{top: 50, right: 0, transform: "translate(50%, -50%)"}} connectioncount={1} id="true"/>
        </div>
        <div>
          {data.result}
        </div>
    </div>
  );
};

