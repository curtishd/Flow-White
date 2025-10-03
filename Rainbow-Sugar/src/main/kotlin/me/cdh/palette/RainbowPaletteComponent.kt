package me.cdh.palette

import com.formdev.flatlaf.util.ScaledEmptyBorder
import com.formdev.flatlaf.util.UIScale
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class RainbowPaletteComponent(private val colorData: RainbowPaletteData, private val itemPainter: RainbowPaletteItemPainter) :
    JComponent() {
    private val items: MutableList<Item?> = ArrayList<Item?>()

    private var hoverIndex = -1
    private var selectedIndex = -1

    init {
        init()
    }

    private fun init() {
        setBorder(ScaledEmptyBorder(10, 10, 5, 10))
        val mouseListener: MouseAdapter = object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    val index = getIndexOf(e.getPoint())
                    if (hoverIndex == index) {
                        setSelectedIndex(index)
                    }
                    if (index != hoverIndex) {
                        val oldHover = hoverIndex
                        hoverIndex = index
                        repaintAt(oldHover)
                        repaintAt(hoverIndex)
                    }
                }
            }

            override fun mouseMoved(e: MouseEvent) {
                val index = getIndexOf(e.getPoint())
                if (hoverIndex != index) {
                    val oldIndex = hoverIndex
                    hoverIndex = index
                    repaintAt(hoverIndex)
                    repaintAt(oldIndex)
                }
            }
        }

        addMouseListener(mouseListener)
        addMouseMotionListener(mouseListener)
    }

    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    fun setSelectedIndex(selectedIndex: Int) {
        val oldIndex = this.selectedIndex
        this.selectedIndex = selectedIndex
        if (oldIndex != selectedIndex) {
            repaintAt(oldIndex)
        }
        repaintAt(selectedIndex)
        selectedIndex(selectedIndex)
    }

    fun getColorAt(index: Int): Color? {
        if (index >= 0 && index < items.size) {
            return items[index]!!.color
        }
        return null
    }

    fun addChangeListener(listener: ChangeListener?) {
        listenerList.add(ChangeListener::class.java, listener)
    }

    private fun getIndexOf(point: Point): Int {
        for (item in items) {
            if (item != null) {
                if (item.size.contains(point)) {
                    return item.index
                }
            }
        }
        return -1
    }

    private fun repaintAt(index: Int) {
        if (index >= 0 && index < items.size) {
            val border = UIScale.scale(itemPainter.getItemBorderSize())
            val itemRec = items[index]!!.size
            val x = itemRec.x - border
            val y = itemRec.y - border
            val width = itemRec.width + border * 2
            val height = itemRec.height + border * 2
            repaint(x, y, width, height)
        }
    }

    private fun selectedIndex(index: Int) {
        if (index >= 0 && index < items.size) {
            fireSelectionChanged(ChangeEvent(this))
        }
    }

    override fun paintComponent(g: Graphics) {
        prepareItemSize()
        var selectedItem: Item? = null
        var focusedItem: Item? = null
        for (item in items) {
            if (item != null) {
                if (item.isSelected) {
                    selectedItem = item
                } else if (item.isHasFocus) {
                    focusedItem = item
                } else {
                    item.paint(g)
                }
            }
        }
        // paint selected and focused item
        selectedItem?.paint(g)
        focusedItem?.paint(g)
        super.paintComponent(g)
    }

    private fun prepareItemSize() {
        val insets = getInsets()
        var x = insets.left
        val y = insets.top
        val width = getWidth() - (insets.left + insets.right)
        val gap = UIScale.scale(itemPainter.getItemGap())
        val itemSize = UIScale.scale(itemPainter.getItemSize())
        val itemCount = colorData.size()
        val column = (width + gap) / (itemSize!!.width + gap)

        val targetWidth = (column * itemSize.width) + ((column - 1) * gap)
        x += (width - targetWidth) / 2

        items.clear()
        for (i in 0..<itemCount) {
            val row = i / column
            val col = i % column

            val color = colorData.get(i)

            val lx = x + col * (itemSize.width + gap)
            val ly = y + row * (itemSize.height + gap)
            items.add(Item(color, i, lx, ly, itemSize.width, itemSize.height))
        }
    }

    override fun getPreferredSize(): Dimension {
        val maxRow = itemPainter.getMaxRow()
        val gap = UIScale.scale(itemPainter.getItemGap())
        val insets = getInsets()
        val itemSize = UIScale.scale(itemPainter.getItemSize())
        val height = (maxRow * itemSize!!.height) + ((maxRow - 1) * gap) + (insets.top + insets.bottom)
        return Dimension(50, height)
    }

    fun fireSelectionChanged(event: ChangeEvent?) {
        val listeners = listenerList.getListenerList()
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === ChangeListener::class.java) {
                (listeners[i + 1] as ChangeListener).stateChanged(event)
            }
            i -= 2
        }
    }

    private inner class Item(
        val color: Color?,
        val index: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ) {
        val size = Rectangle(x, y, width, height)

        fun paint(g: Graphics) {
            val clip = g.clip
            if (clip == null || clip.intersects(size)) {
                val g2 = g.create() as Graphics2D
                g2.clip = g.clip
                g2.translate(size.x, size.y)
                val isSelected = this.isSelected
                val hasFocus = this.isHasFocus
                itemPainter.paintItem(g2, color, size.width, size.height, isSelected, hasFocus)
                g2.dispose()
            }
        }

        val isSelected: Boolean
            get() = selectedIndex == index

        val isHasFocus: Boolean
            get() = hoverIndex == index
    }
}
