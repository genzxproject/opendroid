package com.opendroid.ai.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

/**
 * Lightweight markdown-lite renderer for chat bubbles.
 * Supports: **bold**, `inline code`, ``` ```code blocks``` ```, and blank-line
 * paragraph breaks. That covers the vast majority of LLM output without pulling
 * in a full markdown engine. ponytail: naive regex — a full markdown lib
 * (e.g. compose-markdown) when tables/headings matter.
 */
@Composable
fun MarkdownText(
    text: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    lineHeight: androidx.compose.ui.unit.TextUnit = 21.sp,
    codeColor: Color,
    codeBackground: Color? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = renderMarkdown(text, codeColor),
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        maxLines = maxLines
    )
}

private fun renderMarkdown(text: String, codeColor: Color): AnnotatedString {
    return buildAnnotatedString {
        // Split on code fences first so code blocks are never touched by inline rules
        val parts = text.split(Regex("```"))
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Inside a code fence — render monospace
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        color = codeColor,
                        fontSize = 12.sp
                    )
                ) {
                    append(part.trim('\n'))
                }
                if (index < parts.size - 1) append("\n\n")
            } else {
                appendInlineMarkdown(part, codeColor)
            }
        }
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineMarkdown(
    text: String,
    codeColor: Color
) {
    // Tokenize: `code`, **bold**, then plain text
    val regex = Regex("(`[^`]+`)|(\\*\\*[^*]+\\*\\*)")
    var last = 0
    for (match in regex.findAll(text)) {
        if (match.range.first > last) {
            append(text.substring(last, match.range.first))
        }
        val token = match.value
        when {
            token.startsWith("**") && token.endsWith("**") -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(token.substring(2, token.length - 2))
                }
            }
            token.startsWith("`") && token.endsWith("`") -> {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        color = codeColor,
                        fontSize = 12.sp
                    )
                ) {
                    append(token.substring(1, token.length - 1))
                }
            }
        }
        last = match.range.last + 1
    }
    if (last < text.length) {
        append(text.substring(last))
    }
}
