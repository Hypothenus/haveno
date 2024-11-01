/*
 * This file is part of Haveno.
 *
 * Haveno is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Haveno is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Haveno. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.support.dispute.messages;

import bisq.core.proto.CoreProtoResolver;
import bisq.core.support.SupportType;
import bisq.core.support.dispute.Dispute;
import bisq.core.trade.messages.PaymentSentMessage;
import bisq.network.p2p.NodeAddress;

import java.util.Optional;

import bisq.common.app.Version;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public final class DisputeOpenedMessage extends DisputeMessage {
    private final Dispute dispute;
    private final NodeAddress senderNodeAddress;
    private final String updatedMultisigHex;
    private final PaymentSentMessage paymentSentMessage;

    public DisputeOpenedMessage(Dispute dispute,
                                 NodeAddress senderNodeAddress,
                                 String uid,
                                 SupportType supportType,
                                 String updatedMultisigHex,
                                 PaymentSentMessage paymentSentMessage) {
        this(dispute,
                senderNodeAddress,
                uid,
                Version.getP2PMessageVersion(),
                supportType,
                updatedMultisigHex,
                paymentSentMessage);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private DisputeOpenedMessage(Dispute dispute,
                                  NodeAddress senderNodeAddress,
                                  String uid,
                                  String messageVersion,
                                  SupportType supportType,
                                  String updatedMultisigHex,
                                  PaymentSentMessage paymentSentMessage) {
        super(messageVersion, uid, supportType);
        this.dispute = dispute;
        this.senderNodeAddress = senderNodeAddress;
        this.updatedMultisigHex = updatedMultisigHex;
        this.paymentSentMessage = paymentSentMessage;
    }

    @Override
    public protobuf.NetworkEnvelope toProtoNetworkEnvelope() {
        protobuf.DisputeOpenedMessage.Builder builder = protobuf.DisputeOpenedMessage.newBuilder()
                .setUid(uid)
                .setDispute(dispute.toProtoMessage())
                .setSenderNodeAddress(senderNodeAddress.toProtoMessage())
                .setType(SupportType.toProtoMessage(supportType))
                .setUpdatedMultisigHex(updatedMultisigHex);
        Optional.ofNullable(paymentSentMessage).ifPresent(e -> builder.setPaymentSentMessage(paymentSentMessage.toProtoNetworkEnvelope().getPaymentSentMessage()));
        return getNetworkEnvelopeBuilder().setDisputeOpenedMessage(builder).build();
    }

    public static DisputeOpenedMessage fromProto(protobuf.DisputeOpenedMessage proto,
                                                  CoreProtoResolver coreProtoResolver,
                                                  String messageVersion) {
        return new DisputeOpenedMessage(Dispute.fromProto(proto.getDispute(), coreProtoResolver),
                NodeAddress.fromProto(proto.getSenderNodeAddress()),
                proto.getUid(),
                messageVersion,
                SupportType.fromProto(proto.getType()),
                proto.getUpdatedMultisigHex(),
                proto.hasPaymentSentMessage() ? PaymentSentMessage.fromProto(proto.getPaymentSentMessage(), messageVersion) : null);
    }

    @Override
    public String getTradeId() {
        return dispute.getTradeId();
    }

    @Override
    public String toString() {
        return "DisputeOpenedMessage{" +
                "\n     dispute=" + dispute +
                ",\n     senderNodeAddress=" + senderNodeAddress +
                ",\n     DisputeOpenedMessage.uid='" + uid + '\'' +
                ",\n     messageVersion=" + messageVersion +
                ",\n     supportType=" + supportType +
                ",\n     updatedMultisigHex=" + updatedMultisigHex +
                ",\n     paymentSentMessage=" + paymentSentMessage +
                "\n} " + super.toString();
    }
}
