import {
  Node,
  Position,
  NodeProps,
  Handle,
  useReactFlow,
  NodeResizer,
} from "@xyflow/react";
import "./styles.css";
import LimitedConnectionHandle from "./LimitedConnectionHandle";

type Text = Node<{ code: string; result: string; selected: boolean }>;

export default function NodeIf({ id, data, selected }: NodeProps<Text>) {
  const { updateNodeData } = useReactFlow();
  return (
    <>
      <NodeResizer minHeight={50} minWidth={50} isVisible={selected} />
      <div className="react-flow__node1 nodrag">
        <input
          value={data.code}
          style={{ transform: "rotate(-45dg)" }}
          className="text-input-node__input1"
          placeholder="a > b"
          onChange={(evt) => updateNodeData(id, { code: evt.target.value })}
        ></input>
      </div>
      <br></br>
      <Handle type="target" position={Position.Top} />
      <LimitedConnectionHandle
        type="source"
        position={Position.Left}
        connectioncount={1}
        id="false"
      />
      <LimitedConnectionHandle
        type="source"
        position={Position.Right}
        connectioncount={1}
        id="true"
      />
      <div className="text-input-node__input2"> Вывод: {data.result}</div>
    </>
  );
}
