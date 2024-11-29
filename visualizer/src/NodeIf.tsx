import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer} from "@xyflow/react"
import { memo } from 'react';
import "./styles.css"
import LimitedConnectionHandle from "./LimitedConnectionHandle"

type Text = Node<{code: string, result: string, selected: boolean}>;

export default function NodeIf({id, data, selected}: NodeProps<Text> ) {
  const { updateNodeData } = useReactFlow();
  return (
    <div>
      <body>
      <div className="row">
      <div className="If" style={{transform: "rotate(45deg)"}}></div>
          <input
            className="node-code"
            style={{left: 34}}
            placeholder="a > b"
            value = {data.code}
            onChange={(evt) => updateNodeData(id, { code: evt.target.value })}
          />
          <div className="Output"><div className="Output-node-code">{data.result}</div></div>
          <div>
            <Handle type="target" position={Position.Top} style={{left: 34, transform: "translate(-50%, -50%)"}}/>
            <LimitedConnectionHandle type="source" position={Position.Left}  style={{transform: "translate(-50%, -50%)"}}  connectioncount={1} id="false"/>
            <LimitedConnectionHandle type="source" position={Position.Right} style={{left: 60, transform: "translate(50%, -50%)"}} connectioncount={1} id="true"/>
          </div>
        </div>
      </body>
    </div>
  );
};



