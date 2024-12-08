import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer } from "@xyflow/react";
import "./styles.css";
import LimitedConnectionHandle from "./LimitedConnectionHandle";

type Text = Node<{ code: string; result: string; selected: boolean }>;

export default function NodeCalc({ id, data, selected }: NodeProps<Text>) {
    const { updateNodeData } = useReactFlow();
    return (
        <>
            <NodeResizer minHeight={40} minWidth={100} isVisible={selected} />
            <div style={{ width: "100%", height: "100%" }}>
                <textarea
                    style={{
                        border: "1px solid #ededed",
                        boxSizing: "border-box",
                        width: "100%",
                        height: "100%",
                        textAlign: "left",
                        resize: "none",
                    }}
                    value={data.code}
                    placeholder="c = b + a"
                    onChange={(evt) => updateNodeData(id, { code: evt.target.value })}
                ></textarea>
            </div>
            <div className="node-result" onChange={(evt) => updateNodeData(id, {})}>
                Результат: {data.result}
            </div>
            <Handle type="target" position={Position.Top} />
            <LimitedConnectionHandle type="source" position={Position.Bottom} connectioncount={1} />
        </>
    );
}
