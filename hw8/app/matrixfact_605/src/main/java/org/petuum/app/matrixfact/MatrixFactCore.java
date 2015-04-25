package org.petuum.app.matrixfact;

import org.petuum.app.matrixfact.Rating;
import org.petuum.app.matrixfact.LossRecorder;

import org.petuum.ps.PsTableGroup;
import org.petuum.ps.row.double_.DenseDoubleRow;
import org.petuum.ps.row.double_.DenseDoubleRowUpdate;
import org.petuum.ps.row.double_.DoubleRow;
import org.petuum.ps.row.double_.DoubleRowUpdate;
import org.petuum.ps.table.DoubleTable;
import org.petuum.ps.common.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MatrixFactCore {
    private static final Logger logger =
        LoggerFactory.getLogger(MatrixFactCore.class);

    // Perform a single SGD on a rating and update LTable and RTable
    // accordingly.
    public static void sgdOneRating(Rating r, double learningRate,
            DoubleTable LTable, DoubleTable RTable, int K, double lambda) {
        // get one row from LTable
        DoubleRow LTableRowCache = new DenseDoubleRow(K + 1);
        DoubleRow LTableRow = LTable.get(r.userId);
        LTableRowCache.reset(LTableRow);

        // get one row from RTable
        DoubleRow RTableRowCache = new DenseDoubleRow(K + 1);
        DoubleRow RTableRow = RTable.get(r.prodId);
        RTableRowCache.reset(RTableRow);

        // get n_i and m_j
        double n_i = LTableRowCache.getUnlocked(K);
        double m_j = RTableRowCache.getUnlocked(K);

        // compute e_ij
        double e_ij = r.rating;
        for (int i = 0; i < K - 1; ++i) {
            e_ij += LTableRowCache.getUnlocked(i) * RTableRowCache.getUnlocked(i);
        }
      
        // bulk update LTableRow
        DoubleRowUpdate LTableRowUpdates = new DenseDoubleRowUpdate(K);
        double constant1 = 2 * learningRate;
        double constant2 = lambda / n_i;
        for (int i = 0; i < K - 1; ++i) {
            double delta = contant1 * (e_ij * RTableRowCache.getUnlocked(i) 
                - contant2 * LTableRowCache.getUnlocked(i));
            LTableRowUpdates.setUpdate(i, delta);
        }
        // bulk update RTableRow
        DoubleRowUpdate RTalbeRowUpdates = new DenseDoubleRowUpdate(K);
        double constant3 = lambda / m_j;
        for (int i = 0; i < K - 1; ++i) {
            double delta = constant1 * (e_ij * LTableRowCache.getUnlocked(i) 
                - constant3 * RTableRowCache.getUnlocked(i));
        }
        // commit update on LTable and RTable
        LTable.batchInc(r.userId, LTableRowUpdates);
        RTable.batchInc(r.prodId, RTableRowUpdates);
    }

    // Evaluate square loss on entries [elemBegin, elemEnd), and L2-loss on of
    // row [LRowBegin, LRowEnd) of LTable,  [RRowBegin, RRowEnd) of Rtable.
    // Note the interval does not include LRowEnd and RRowEnd. Record the loss to
    // lossRecorder.
    public static void evaluateLoss(ArrayList<Rating> ratings, int ithEval,
            int elemBegin, int elemEnd, DoubleTable LTable,
            DoubleTable RTable, int LRowBegin, int LRowEnd, int RRowBegin,
            int RRowEnd, LossRecorder lossRecorder, int K, double lambda) {
        double sqLoss = 0;
        double totalLoss = 0;
        
        sqLoss = computeSqLoss(ratings, elemBegin, elemEnd, LTable, RTable, K);
        totalLoss += computeL2Loss(LRowBegin, LRowEnd, LTable, K);
        totalLoss += computeSqLoss(RRowBegin, RRowEnd, RTable, K);
        totalLoss *= lambda;

        lossRecorder.incLoss(ithEval, "SquareLoss", sqLoss);
        lossRecorder.incLoss(ithEval, "FullLoss", totalLoss);
        lossRecorder.incLoss(ithEval, "NumSamples", elemEnd - elemBegin);
    }

    private static double computeSqLoss(ArrayList<Rating> ratings, 
        int elemBegin, int elemEnd, DoubleTable LTable,
        DoubleTable RTable, int K) {
        double sqLoss = 0;

        // square loss on entries [elembegin, elemEnd)
        for (int i = elemBegin; i < elemEnd; ++i) {
            Rating r = ratings.get(i);
            // get one row from LTable
            DoubleRow LTableRowCache = new DenseDoubleRow(K + 1);
            DoubleRow LTableRow = LTable.get(r.userId);
            LTableRowCache.reset(LTableRow);

            // get one row from RTable
            DoubleRow RTableRowCache = new DenseDoubleRow(K + 1);
            DoubleRow RTableRow = RTable.get(r.prodId);
            RTableRowCache.reset(RTableRow);
            
            double sum = 0;
            for (int j = 0; j < K; ++j) {
                sum += LTableRowCache.getUnlocked(i) 
                  * RTableRowCache.getUnlocked(i);
            }

            sqLoss += Math.pow(r.rating - sum, 2);
        }

        return sqLoss;
    }

    private static double computeL2Loss(int rowBegin, int rowEnd, 
        DoubleTable table, int K) {
      double L2Loss = 0;
      for (int i = rowBegin; i < RowEnd; ++i) {
          DoubleRow rowCache = new DenseDoubleRow(K + 1);
          DoubleRow aRow = table.get(i);
          rowCache.reset(aRow);
          for (int j = 0; j < K; ++j) {
              double val = rowCache.getUnlocked(j);
              if (val != 0) {
                  L2Loss += Math.pow(val, 2);
              }
          }
      }

      return L2Loss;
    }
}
