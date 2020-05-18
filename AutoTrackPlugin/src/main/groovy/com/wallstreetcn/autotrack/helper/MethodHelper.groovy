package com.wallstreetcn.autotrack.helper

import org.objectweb.asm.Opcodes

class MethodHelper {
    public final static String INJECT_CLASS_NAME = "com/wallstreetcn/sample/ToastHelper"
    public final static HashMap<String, MethodCell> sInterfaceMethods = new HashMap<>()
    static {
        sInterfaceMethods.put('onClick(Landroid/view/View;)V', new MethodCell(
                'onClick',
                '(Landroid/view/View;)V',
                'android/view/View$OnClickListener',
                'toast',
                '(Ljava/lang/Object;Landroid/view/View;Ljava/lang/Object;)V',
                1, 1,
                [Opcodes.ALOAD]))
        sInterfaceMethods.put('onClick(Landroid/content/DialogInterface;I)V', new MethodCell(
                'onClick',
                '(Landroid/content/DialogInterface;I)V',
                'android/content/DialogInterface$OnClickListener',
                'onClick',
                '(Ljava/lang/Object;Landroid/content/DialogInterface;I)V',
                0, 3,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD]))
        sInterfaceMethods.put('onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V', new MethodCell(
                'onItemClick',
                '(Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                'android/widget/AdapterView$OnItemClickListener',
                'onItemClick',
                '(Ljava/lang/Object;Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                0, 5,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.LLOAD]))
        sInterfaceMethods.put('onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V', new MethodCell(
                'onItemSelected',
                '(Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                'android/widget/AdapterView$OnItemSelectedListener',
                'onItemSelected',
                '(Ljava/lang/Object;Landroid/widget/AdapterView;Landroid/view/View;IJ)V',
                0, 5,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.LLOAD]))
        sInterfaceMethods.put('onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z', new MethodCell(
                'onGroupClick',
                '(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z',
                'android/widget/ExpandableListView$OnGroupClickListener',
                'onGroupClick',
                '(Ljava/lang/Object;Landroid/widget/ExpandableListView;Landroid/view/View;IJ)V',
                0, 5,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.LLOAD]))
        sInterfaceMethods.put('onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z', new MethodCell(
                'onChildClick',
                '(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z',
                'android/widget/ExpandableListView$OnChildClickListener',
                'onChildClick',
                '(Ljava/lang/Object;Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)V',
                0, 6,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.LLOAD]))
        sInterfaceMethods.put('onRatingChanged(Landroid/widget/RatingBar;FZ)V', new MethodCell(
                'onRatingChanged',
                '(Landroid/widget/RatingBar;FZ)V',
                'android/widget/RatingBar$OnRatingBarChangeListener',
                'onRatingChanged',
                '(Ljava/lang/Object;Landroid/widget/RatingBar;FZ)V',
                0, 4,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.FLOAD, Opcodes.ILOAD]))
        sInterfaceMethods.put('onStopTrackingTouch(Landroid/widget/SeekBar;)V', new MethodCell(
                'onStopTrackingTouch',
                '(Landroid/widget/SeekBar;)V',
                'android/widget/SeekBar$OnSeekBarChangeListener',
                'onStopTrackingTouch',
                '(Ljava/lang/Object;Landroid/widget/SeekBar;)V',
                0, 2,
                [Opcodes.ALOAD, Opcodes.ALOAD]))
        sInterfaceMethods.put('onCheckedChanged(Landroid/widget/CompoundButton;Z)V', new MethodCell(
                'onCheckedChanged',
                '(Landroid/widget/CompoundButton;Z)V',
                'android/widget/CompoundButton$OnCheckedChangeListener',
                'onCheckedChanged',
                '(Ljava/lang/Object;Landroid/widget/CompoundButton;Z)V',
                0, 3,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD]))
        sInterfaceMethods.put('onCheckedChanged(Landroid/widget/RadioGroup;I)V', new MethodCell(
                'onCheckedChanged',
                '(Landroid/widget/RadioGroup;I)V',
                'android/widget/RadioGroup$OnCheckedChangeListener',
                'onCheckedChanged',
                '(Ljava/lang/Object;Landroid/widget/RadioGroup;I)V',
                0, 3,
                [Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD]))

    }
}
