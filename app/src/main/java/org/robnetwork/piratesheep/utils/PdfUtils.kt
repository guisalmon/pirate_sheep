package org.robnetwork.piratesheep.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument

object PdfUtils {
    fun generatePdf(pages: Pair<Bitmap, Bitmap>): PdfDocument = PdfDocument().let { pdf ->
        makePdfPage(pages.first, pdf, 1)
        makePdfPage(pages.second, pdf, 2)
        return pdf
    }

    private fun makePdfPage(bitmap: Bitmap, pdf: PdfDocument, pageNumber: Int) {
        PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, pageNumber)
            .create()
            .let { page1Info ->
                pdf.startPage(page1Info).let { page ->
                    page.canvas.let { canvas ->
                        Paint().let { paint ->
                            paint.color = Color.BLACK
                            canvas.drawBitmap(bitmap, 0f, 0f, paint)
                        }
                    }
                    pdf.finishPage(page)
                }
            }
    }
}