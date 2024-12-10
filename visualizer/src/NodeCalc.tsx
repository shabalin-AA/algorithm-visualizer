import { Node, Position, NodeProps, Handle, useReactFlow, NodeResizer } from "@xyflow/react";
import "./styles.css";
import LimitedConnectionHandle from "./LimitedConnectionHandle";
import { useEffect, useState } from "react";

type Text = Node<{ code: string; result: string; selected: boolean }>;

export default function NodeCalc({ id, data, selected }: NodeProps<Text>) {
    const { updateNodeData } = useReactFlow();
    const [code, setCode] = useState(data.code);

    const handleCodeChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setCode(event.target.value);
    };

    useEffect(() => {
        setCode(data.code);
    }, [data.code]);

    return (
        <>
            <NodeResizer minHeight={40} minWidth={100} isVisible={selected} />
            <div style={{ width: "100%", height: "100%", minWidth: "100px", minHeight: "40px" }}>
                <textarea
                    className="NodeCalc-code"
                    value={code}
                    placeholder="c = b + a"
                    onChange={handleCodeChange}
                    onBlur={() => {
                        updateNodeData(id, { code: code });
                    }}
                ></textarea>
            </div>
            {data.result !== "" && (
                <div className="node-result" onChange={(evt) => updateNodeData(id, {})}>
                    {data.result}
                </div>
            )}
            <Handle type="target" position={Position.Top} />
            <LimitedConnectionHandle type="source" position={Position.Bottom} connectioncount={1} />
        </>
    );
}
