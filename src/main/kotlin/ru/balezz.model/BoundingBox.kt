package ru.balezz.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
data class BoundingBox(
        @XmlElement val xmin:  Int,
        @XmlElement val ymin:  Int,
        @XmlElement val xmax:  Int,
        @XmlElement val ymax:  Int,
        @XmlElement val depth: Int
) {
    constructor() : this(0, 0, 0, 0, 0)
}