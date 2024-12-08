import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer } from "@xyflow/react";
import "./styles.css";
import LimitedConnectionHandle from "./LimitedConnectionHandle";

type Text = Node<{ code: string; result: string; selected: boolean }>;

export default function NodeIf({ id, data, selected }: NodeProps<Text>) {
    const { updateNodeData } = useReactFlow();
    return (
        <>
            <NodeResizer minHeight={100} minWidth={100} isVisible={selected} />
            <div style={{ width: "100%", height: "100%" }}>
                <svg
                    viewBox="0 0 100 100"
                    style={{
                        position: "absolute",
                        width: "100%",
                        height: "100%",
                        transform: "translate(-50%, 0%)",
                        scale: "inherit",
                    }}
                >
                    <polygon points="0,50 50,0 100,50 50,100" fill="white" stroke="black" />
                </svg>
                <input
                    value={data.code}
                    placeholder="a > b"
                    onChange={(evt) => updateNodeData(id, { code: evt.target.value })}
                    style={{
                        position: "absolute",
                        top: "50%",
                        left: "50%",
                        transform: "translate(-50%, -50%)",
                        width: "50%",
                        height: "20%",
                        border: "1px solid #ccc",
                        borderRadius: "4px",
                        padding: "5px",
                        boxSizing: "border-box",
                        zIndex: 1,
                        textAlign: "center",
                    }}
                />
            </div>
            <div className="node-result" onChange={(evt) => updateNodeData(id, {})}>
                Результат: {data.result}
            </div>
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
        </>
    );
}
