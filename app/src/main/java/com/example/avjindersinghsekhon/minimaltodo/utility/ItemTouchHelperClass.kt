package com.example.avjindersinghsekhon.minimaltodo.utility

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperClass(
        private val adapter: ItemTouchHelperAdapter
) : ItemTouchHelper.Callback() {
    interface ItemTouchHelperAdapter {
        fun onItemMoved(fromPosition: Int, toPosition: Int)
        fun onItemRemoved(position: Int)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val upFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(upFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemRemoved(viewHolder.adapterPosition)
    } //    @SuppressWarnings("deprecation")
    //    @Override
    //    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    //        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
    //            View itemView = viewHolder.itemView;
    //            Paint p = new Paint();
    //
    //
    //            MainActivity.BasicListAdapter.ViewHolder vh= (MainActivity.BasicListAdapter.ViewHolder)viewHolder;
    //            p.setColor(recyclerView.getResources().getColor(R.color.primary_light));
    //
    //            if(dX > 0){
    //                c.drawRect((float)itemView.getLeft(), (float)itemView.getTop(), dX, (float)itemView.getBottom(), p);
    //                String toWrite = "Left"+itemView.getLeft()+" Top "+itemView.getTop()+" Right "+dX+" Bottom "+itemView.getBottom();
    ////                Log.d("OskarSchindler", toWrite);
    //            }
    //            else{
    //                String toWrite = "Left"+(itemView.getLeft()+dX)+" Top "+itemView.getTop()+" Right "+dX+" Bottom "+itemView.getBottom();
    ////                Log.d("OskarSchindler", toWrite);
    //                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), p);
    //            }
    //            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    //        }
    //    }
}
