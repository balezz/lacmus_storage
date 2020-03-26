package ru.balezz.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "size")
@XmlAccessorType(XmlAccessType.FIELD)
data class Size(
        @XmlElement val width: Int,
        @XmlElement val height: Int,
        @XmlElement val depth: Int
) {
    constructor(): this(0, 0, 0)
}