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

export default function NodeCalc({ id, data, selected }: NodeProps<Text>) {
  const { updateNodeData } = useReactFlow();
  return (
    <>
      <NodeResizer minHeight={50} minWidth={50} isVisible={selected} />
      <textarea
        value={data.code}
        style={{ transform: "rotate(0deg)" }}
        className="text-input-node__input1 nodrag"
        placeholder="c = b + a"
        onChange={(evt) => updateNodeData(id, { code: evt.target.value })}
      ></textarea>
      <Handle type="target" position={Position.Top} style={{}} />
      <LimitedConnectionHandle
        type="source"
        position={Position.Bottom}
        style={{}}
        connectioncount={1}
      />
      <div className="text-input-node__input2"> Вывод: {data.result}</div>
    </>
  );
}
