const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type",
};

function jsonResponse(data, status = 200) {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      ...corsHeaders,
      "Content-Type": "application/json",
    },
  });
}

export default {
  async fetch(request, env) {
    // CORS preflight
    if (request.method === "OPTIONS") {
      return new Response(null, {
        status: 204,
        headers: corsHeaders,
      });
    }

    // Check Worker status
    if (request.method === "GET") {
      return jsonResponse({
        status: "online",
        assistant: "GULFAM AI",
        message: "GULFAM is ready!"
      });
    }

    // Only POST allowed for chat
    if (request.method !== "POST") {
      return jsonResponse({
        error: "Method not allowed"
      }, 405);
    }

    try {
      // Check API key
      if (!env.XAI_API_KEY) {
        return jsonResponse({
          error: "Grok API key is missing"
        }, 500);
      }

      // Read Android request
      const body = await request.json();
      const message = body.message;

      if (
        typeof message !== "string" ||
        !message.trim()
      ) {
        return jsonResponse({
          error: "Please enter a message"
        }, 400);
      }

      if (message.length > 4000) {
        return jsonResponse({
          error: "Message is too long"
        }, 400);
      }

      // Connect to Grok AI
      const grokResponse = await fetch(
        "https://api.x.ai/v1/chat/completions",
        {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${env.XAI_API_KEY}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            model: "grok-4.7",
            messages: [
              {
                role: "system",
                content: `You are GULFAM, a powerful personal AI voice assistant.

Your personality:
- Friendly, intelligent and respectful.
- Speak naturally like a human assistant.
- Keep answers clear and helpful.
- Respond in the same language as the user.
- Understand English, Urdu and Roman Urdu.
- Help with questions, planning and daily tasks.
- Never claim to perform phone actions unless they are actually completed.
- Your name is GULFAM AI.`
              },
              {
                role: "user",
                content: message.trim()
              }
            ],
            temperature: 0.7
          })
        }
      );

      const result = await grokResponse.json();

      if (!grokResponse.ok) {
        return jsonResponse({
          error: "Grok API error",
          details: result.error?.message || "Request failed"
        }, grokResponse.status);
      }

      const reply =
        result.choices?.[0]?.message?.content;

      if (!reply) {
        return jsonResponse({
          error: "Grok returned an empty response"
        }, 502);
      }

      // Send answer back to Android
      return jsonResponse({
        status: "success",
        assistant: "GULFAM AI",
        reply: reply
      });

    } catch (error) {
      return jsonResponse({
        error: "Server error",
        details: error.message
      }, 500);
    }
  }
};
