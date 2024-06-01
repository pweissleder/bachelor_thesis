package ba.pascal.weissleder.server.model.framework.support.devices

import java.io.Serializable as JavaSerializable

// The structure of the following classes is based to the Unicorn Project

data class Endpoint(
    val id: Int = -1,    // -1 should indicate Error. This is a Breaking Value for the Matter Specification
    val clusters: List<Cluster> = emptyList()
) : JavaSerializable


data class Cluster(
    val type: ClusterType = ClusterType.Unknown,

    val commands: List<CommandType> = emptyList(),

    val attributes: List<Attribute<JavaSerializable?>> = emptyList()
) : JavaSerializable

data class Attribute<A : JavaSerializable?>(
    val type: AttributeType = AttributeType.Unknown,

    val value: A? = null
) : JavaSerializable {
    override fun toString(): String {
        return "Attribute(type=$type, value=$value)"
    }
}

enum class ClusterType : JavaSerializable {
    BasicInformation, OnOff, FlowMeasurement,
    Unknown; // should indicate Error. This is a Breaking Value for the Matter Specification
}

enum class CommandType : JavaSerializable {
    On, Off, Toggle, // OnOff cluster
    MoveToLevel, Move,
    Unknown; //Should indicate Error. This is a Breaking Value for the Matter Specification
}

enum class AttributeType : JavaSerializable {
    VendorName, ProductName, SerialNumber, // BasicInformation cluster
    OnOff, // OnOff cluster
    MeasuredValue, // Flow measurement
    Unknown; // Should indicate Error. This is a Breaking Value for the Matter Specification
}




