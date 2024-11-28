import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer } from "@xyflow/react"
import "./styles.css"
import LimitedConnectionHandle from "./LimitedConnectionHandle"

type Text = Node<{code: string, result: string, selected: boolean}>;

export default function NodeCalc({id, data, selected}: NodeProps<Text>) {
  const { updateNodeData } = useReactFlow();
  return (
    <div>
      <body> 
        <div className="row">
          <div className="Calc">
            <textarea
            value={data.code}
            className="node-code"
            placeholder="c = b + a"
            onChange={(evt) => updateNodeData(id, {code: evt.target.value})}
            />
          </div>
          <div className="Output"><div className="Output-node-code">{data.result}</div></div>
          <div>
            <Handle type="target" position={Position.Top} style={{left: 110, top: 8, transform: "translate(-50%, -50%)"}}/>
            <LimitedConnectionHandle type="source" position={Position.Bottom} style={{left: 110, top: 60, transform: "translate(-50%, -50%)"}} connectioncount={1}/>
          </div>
        </div>
      </body>
    </div>
  );
};

